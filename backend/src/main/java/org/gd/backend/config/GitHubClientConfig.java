package org.gd.backend.config;

import lombok.Builder;
import org.gd.backend.exception.GitHubResourceNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class GitHubClientConfig {

    @Bean
    public RestClient gitHubRestClient(RestClient.Builder builder, GitHubProperties props) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(15));
        return builder
                .baseUrl(props.apiUrl())
                .defaultHeader("Authorization", "Bearer " + props.token())
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .defaultStatusHandler(
                        status->status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        (request, response) -> {throw new GitHubResourceNotFoundException(request.getURI().getPath());}
                )
                .requestFactory(requestFactory)
                .build();
    }
}