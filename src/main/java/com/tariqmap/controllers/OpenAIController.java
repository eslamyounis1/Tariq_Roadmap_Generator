
package com.tariqmap.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/v1/openai")
public class OpenAIController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIController.class);

    @Value("${openai.api.key}")
    private String apiKey;

    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/generate-skills")
    public ResponseEntity<?> generateSkills(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");

        if (topic == null || topic.isEmpty()) {
            return ResponseEntity.badRequest().body("Topic cannot be empty.");
        }

        String prompt = String.format(
                "Generate a concise list of essential skills needed to learn %s. " +
                        "Format the response as a JSON array with 'name' and 'description' fields. " +
                        "Keep it focused and practical.",
                topic
        );

        try {
            String responseBody = callOpenAiApi(prompt);
            logger.info("Raw OpenAI API Response: {}", responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<>() {});
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");

            String jsonContent = content.replaceAll("```json", "").replaceAll("```", "").trim();
            List<Map<String, String>> skills = objectMapper.readValue(jsonContent, new TypeReference<>() {});
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            logger.error("Error generating skills: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch skills from OpenAI API. Error: " + e.getMessage());
        }
    }


@PostMapping("/generate-resources")
public ResponseEntity<?> generateResources(@RequestBody Map<String, String> request) {
    String skillName = request.get("skillName");

    if (skillName == null || skillName.isEmpty()) {
        return ResponseEntity.badRequest().body("Skill name cannot be empty.");
    }

    String prompt = String.format(
            "Suggest 3 high-quality learning resources for %s. " +
                    "Include only legitimate, well-known platforms. " +
                    "Format as a JSON array of objects with 'title', 'url', and 'type' fields.",
            skillName
    );

    try {
        // Call OpenAI API
        String responseBody = callOpenAiApi(prompt);
        logger.info("Raw OpenAI API Response for skill '{}': {}", skillName, responseBody);

        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the OpenAI response
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<>() {});
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("No choices found in OpenAI API response.");
        }

        // Extract the 'content' field from the first choice
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) message.get("content");

        // Parse the 'content' field as a JSON array
        List<Map<String, String>> resources = objectMapper.readValue(content, new TypeReference<>() {});

        // Return the resources
        return ResponseEntity.ok(resources);

    } catch (Exception e) {
        logger.error("Error generating resources for skill '{}': {}", skillName, e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to fetch resources for skill: " + skillName + ". Error: " + e.getMessage());
    }
}


    private String callOpenAiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("OpenAI API returned non-OK status: {}", response.getStatusCode());
                throw new RuntimeException("OpenAI API request failed with status: " + response.getStatusCode());
            }

            return response.getBody();
        } catch (Exception e) {
            logger.error("Error during OpenAI API call: {}", e.getMessage(), e);
            throw new RuntimeException("OpenAI API call failed. Error: " + e.getMessage());
        }
    }
}
