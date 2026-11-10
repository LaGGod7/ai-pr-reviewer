package org.gd.backend.client;

import org.gd.backend.dto.ai.AiReviewRequest;
import org.gd.backend.dto.ai.AiReviewResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiReviewClient {

    private final RestClient restClient;

    public AiReviewClient(@Qualifier("aiServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }
    public AiReviewResponse review(AiReviewRequest request) {
        return restClient.post()
                .uri("/api/v1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AiReviewResponse.class);
    }
}
