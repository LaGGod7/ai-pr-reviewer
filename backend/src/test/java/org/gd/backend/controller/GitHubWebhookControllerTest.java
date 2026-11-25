package org.gd.backend.controller;

import org.gd.backend.webhook.ReviewDeduplicator;
import org.gd.backend.webhook.WebhookReviewRunner;
import org.gd.backend.webhook.WebhookSignatureVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GitHubWebhookController.class)
class GitHubWebhookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookSignatureVerifier signatureVerifier;

    @MockitoBean
    private WebhookReviewRunner reviewRunner;

    @MockitoBean
    private ReviewDeduplicator deduplicator;

    private static final String OPENED_EVENT = """
            {
            "action":"opened",
            "number":8,
            "pull_request":{"head":{"sha":"abc123"}},
            "repository":{"name":"hello","owner":{"login":"octo"}}
            }
            """;
    private ResultActions sendWebhook(String body) throws Exception {
        return mockMvc.perform(post("/api/v1/webhooks/github")
                .header("X-GitHub-Event","pull_request")
                .header("X-GitHub-Delivery", "test-delivery")
                .header("X-Hub-Signature-256", "sha256=anything")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

    }
    @Test
    void invalidSignature_returns401_andDoesNotReview() throws Exception{
        when(signatureVerifier.isValid(any(),any())).thenReturn(false);

        sendWebhook(OPENED_EVENT).andExpect(status().isUnauthorized());
        verify(reviewRunner,never()).runReview(any(),any(),anyInt(),any());
    }
    @Test
    void openedPr_newCommit_startsReview() throws Exception {
        when(signatureVerifier.isValid(any(), any())).thenReturn(true);
        when(deduplicator.markIfNew("octo", "hello", 8, "abc123")).thenReturn(true);

        sendWebhook(OPENED_EVENT).andExpect(status().isAccepted());

        verify(reviewRunner).runReview("octo", "hello", 8, "abc123");
    }

    @Test
    void openedPr_duplicateCommit_isSkipped() throws Exception {
        when(signatureVerifier.isValid(any(), any())).thenReturn(true);
        when(deduplicator.markIfNew("octo", "hello", 8, "abc123")).thenReturn(false);

        sendWebhook(OPENED_EVENT).andExpect(status().isAccepted());

        verify(reviewRunner, never()).runReview(any(), any(), anyInt(), any());
    }

    @Test
    void closedPr_isIgnored() throws Exception {
        when(signatureVerifier.isValid(any(), any())).thenReturn(true);
        String closedEvent = OPENED_EVENT.replace("\"opened\"", "\"closed\"");

        sendWebhook(closedEvent).andExpect(status().isAccepted());

        verify(deduplicator, never()).markIfNew(any(), any(), anyInt(), any());
        verify(reviewRunner, never()).runReview(any(), any(), anyInt(), any());
    }



}