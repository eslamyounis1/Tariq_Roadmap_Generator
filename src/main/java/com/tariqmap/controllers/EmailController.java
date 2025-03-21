
package com.tariqmap.controllers;

import com.tariqmap.services.EmailService;
import com.tariqmap.services.PdfGeneratorService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @PostMapping("/send-roadmap-email")
    public ResponseEntity<?> sendRoadmapEmail(@RequestBody Map<String, Object> request) {
        try {
            String topic = (String) request.get("topic");
            String email = (String) request.get("email");
            List<Map<String, String>> skills = (List<Map<String, String>>) request.get("skills");

            if (topic == null || topic.isEmpty() || email == null || email.isEmpty() || skills == null || skills.isEmpty()) {
                return ResponseEntity.badRequest().body("Topic, email, and skills are required.");
            }

            // Generate the roadmap PDF
            byte[] pdfContent = pdfGeneratorService.generateRoadmapPdf(topic, skills);

            // Send the email with the PDF attachment
            emailService.sendEmailWithAttachment(
                    email,
                    "Your Roadmap for " + topic,
                    "Dear User,\n\nPlease find attached your roadmap for learning " + topic + ".\n\nBest regards,\nTariq Team",
                    pdfContent
            );

            logger.info("Email sent successfully to: {}", email);
            return ResponseEntity.ok("Roadmap sent to " + email);
        } catch (MessagingException e) {
            logger.error("Failed to send email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email. Please try again.");
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
