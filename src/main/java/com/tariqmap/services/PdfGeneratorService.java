
package com.tariqmap.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class PdfGeneratorService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "http://localhost:8080/api/v1/openai/generate-resources";

    public byte[] generateRoadmapPdf(String topic, List<Map<String, String>> skills) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();
        document.add(new Paragraph("Roadmap for: " + topic));
        document.add(new Paragraph("\nSkills:"));

        for (Map<String, String> skill : skills) {
            String skillName = skill.get("name");
            String skillDescription = skill.get("description");

            // Add skill details
            document.add(new Paragraph("Skill: " + skillName));
            document.add(new Paragraph("Description: " + skillDescription));
            document.add(new Paragraph("\nLearning Resources:"));

            // Fetch resources for the skill
            List<Map<String, String>> resources = fetchResourcesForSkill(skillName);
            if (resources != null && !resources.isEmpty()) {
                for (Map<String, String> resource : resources) {
                    document.add(new Paragraph(" - " + resource.get("title") + " (" + resource.get("type") + ")"));
                    document.add(new Paragraph("   URL: " + resource.get("url")));
                }
            } else {
                document.add(new Paragraph("No resources available."));
            }

            document.add(new Paragraph("\n"));
        }

        document.close();
        return outputStream.toByteArray();
    }

    private List<Map<String, String>> fetchResourcesForSkill(String skillName) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    BASE_URL,
                    Map.of("skillName", skillName),
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            }
        } catch (Exception e) {
            System.err.println("Error fetching resources for skill " + skillName + ": " + e.getMessage());
        }

        return null;
    }
}
