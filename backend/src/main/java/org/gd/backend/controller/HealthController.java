package org.gd.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.gd.backend.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    private final String serviceName;
    public HealthController(@Value("${spring.application.name}") String serviceName) {
        this.serviceName = serviceName;
    }

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "pr-reviewer-backend");

    }
}
