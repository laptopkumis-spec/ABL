package com.aufi.emailservice.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aufi.emailservice.service.EmailService;
import com.aufi.emailservice.service.EmailService.EmailMessage;

/**
 * Endpoint untuk melihat "email" yang dihasilkan dari event order. Berguna untuk
 * verifikasi lewat Insomnia tanpa perlu SMTP asli.
 */
@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public List<EmailMessage> getAll() {
        return emailService.getOutbox();
    }

    @GetMapping("/count")
    public Map<String, Object> count() {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("service", "email-service");
        resp.put("jumlahEmail", emailService.count());
        return resp;
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clear() {
        emailService.clear();
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", "outbox email dikosongkan");
        return ResponseEntity.ok(resp);
    }
}
