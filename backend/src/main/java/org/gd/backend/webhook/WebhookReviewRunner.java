package org.gd.backend.webhook;

import org.gd.backend.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WebhookReviewRunner {
    private static final Logger log = LoggerFactory.getLogger(WebhookReviewRunner.class);
    private final ReviewService reviewService;
    private final ReviewDeduplicator  deduplicator;
    public WebhookReviewRunner(ReviewService reviewService, ReviewDeduplicator deduplicator) {
        this.reviewService = reviewService;
        this.deduplicator = deduplicator;
    }

    @Async
    public void runReview(String owner,String repo , int number,String sha){
        log.info("Starting background review of {}/{}#{}",owner,repo,number);
        try{
            reviewService.reviewPullRequest(owner,repo,number);
            log.info("Finished background review of {}/{}#{}",owner,repo,number);
        }catch (Exception e)
            {
             log.error("Background review failed for {}/{}#{}",owner,repo,number,e);
                deduplicator.release(owner, repo, number, sha);
            }
        }

}
