package org.gd.backend.config;

import lombok.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GitHubClientConfig {

    @Bean
    public RestClient gitHubRestClient(RestClient.Builder builder, GitHubProperties props) {
        return builder
                .baseUrl(props.apiUrl())
                .defaultHeader("Authorization", "Bearer " + props.token())
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }
}