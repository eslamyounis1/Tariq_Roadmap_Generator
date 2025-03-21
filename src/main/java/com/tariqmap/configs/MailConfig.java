
package com.tariqmap.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); // Replace with your SMTP server
        mailSender.setPort(587); // Default SMTP port for TLS

        mailSender.setUsername("eslamjonas1@gmail.com"); // Replace with your email
        mailSender.setPassword("Your-Email-provider-APP-Password"); // Replace with your app password

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // Enable detailed logs

        return mailSender;
    }
}
