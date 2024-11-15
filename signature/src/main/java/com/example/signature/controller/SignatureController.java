package com.example.signature.controller;

import com.example.signature.service.SignatureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SignatureController {

    private final SignatureService signatureService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SignatureController(SignatureService signatureService, ObjectMapper objectMapper) {
        this.signatureService = signatureService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateSignature(@RequestBody Map<String, Object> payload) {
        try {
            JSONObject response = signatureService.generateSignature(payload, "UTF-8");
            // Convert the JSONObject to a pretty-printed JSON string
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.toMap());
            return ResponseEntity.ok(prettyJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Failed to generate signature\"}");
        }
    }
}
