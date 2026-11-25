package org.gd.backend.controller;

import org.gd.backend.webhook.PullRequestEvent;
import org.gd.backend.webhook.ReviewDeduplicator;
import org.gd.backend.webhook.WebhookReviewRunner;
import org.gd.backend.webhook.WebhookSignatureVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/webhooks")
public class GitHubWebhookController {
    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookController.class);

    private final WebhookSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;
    private final WebhookReviewRunner reviewRunner;
    private final ReviewDeduplicator reviewDeduplicator;

    private static final Set<String> REVIEW_ACTIONS = Set.of("opened","synchronize","reopened");

    public GitHubWebhookController(WebhookSignatureVerifier signatureVerifier,ObjectMapper objectMapper,WebhookReviewRunner  reviewRunner,ReviewDeduplicator reviewDeduplicator) {
        this.reviewRunner = reviewRunner;
        this.signatureVerifier = signatureVerifier;this.objectMapper = objectMapper;
        this.reviewDeduplicator = reviewDeduplicator;
    }


    @PostMapping("/github")
    public ResponseEntity<Void> receive(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestHeader("X-GitHub-Delivery") String deliveryId,
            @RequestHeader(value = "X-Hub-Signature-256",required = false)String signature,
            @RequestBody String payload){

        if(!signatureVerifier.isValid(payload,signature)){
            log.warn("Rejected webhook with invalid signature: delivery={}",deliveryId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!"pull_request".equals(event)){
            log.info("Ignoring {} event,deliveryId={}",event,deliveryId);
            return ResponseEntity.accepted().build();
        }

        PullRequestEvent pr = objectMapper.readValue(payload,PullRequestEvent.class);

        if(!REVIEW_ACTIONS.contains(pr.action())){
            log.info("Ignoring pull_request action '{}' for PR #{}",pr.action(),pr.number());
            return ResponseEntity.accepted().build();
        }
        String owner = pr.repository().owner().login();
        String repo = pr.repository().name();
        String sha = pr.pullRequest().head().sha();

        log.info("Review needed: {}/{}#{} at commit {}", owner, repo, pr.number(), sha);
        if(!reviewDeduplicator.markIfNew(owner, repo, pr.number(), sha)){
            log.info("Skipping duplicate review of {}/{}#{} at commit {}", owner, repo, pr.number(), sha); return ResponseEntity.accepted().build();
        }
        reviewRunner.runReview(owner, repo, pr.number(),sha);
        return ResponseEntity.accepted().build();

    }



}
