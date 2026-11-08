package org.gd.backend.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Validated
@EnableConfigurationProperties(GitHubProperties.class)
@ConfigurationProperties(prefix = "github")
public record GitHubProperties(@NotBlank String apiUrl, @NotBlank String token) {
}


