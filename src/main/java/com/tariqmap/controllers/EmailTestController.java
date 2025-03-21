package com.tariqmap.controllers;

import com.tariqmap.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email-test")
public class EmailTestController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> testSendEmail() {
        try {
            emailService.sendEmailWithAttachment(
                    "eslamjonas1@gmail.com",
                    "Test Email",
                    "This is a test email with an attachment.",
                    "Sample PDF Content".getBytes()
            );
            return ResponseEntity.ok("Email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
