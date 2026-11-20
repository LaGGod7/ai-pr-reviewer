package org.gd.backend.service;

import org.gd.backend.dto.github.GitHubReviewResponse;
import org.gd.backend.exception.GitHubResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.gd.backend.entity.ReviewEntity;
import org.gd.backend.repository.ReviewRepository;
import org.gd.backend.client.AiReviewClient;
import org.gd.backend.dto.PullRequestDetailsResponse;
import org.gd.backend.dto.PullRequestReviewResponse;
import org.gd.backend.dto.StoredReviewResponse;
import org.gd.backend.dto.ai.AiReviewRequest;
import org.gd.backend.dto.ai.AiReviewResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


import java.util.List;

@Service
public class ReviewService {

    private final GitHubService gitHubService;
    private final AiReviewClient aiReviewClient;
    private final ReviewRepository reviewRepository;
    private final ReviewPublisher reviewPublisher;

    public ReviewService(GitHubService gitHubService,ReviewPublisher reviewPublisher, AiReviewClient aiReviewClient, ReviewRepository reviewRepository) {
        this.gitHubService = gitHubService;
        this.aiReviewClient = aiReviewClient;
        this.reviewRepository = reviewRepository;
        this.reviewPublisher = reviewPublisher;
    }
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    public PullRequestReviewResponse reviewPullRequest(String owner , String repo ,int number){
        PullRequestDetailsResponse pr = gitHubService.getPullRequestDetails(owner,repo,number);
        AiReviewResponse ai = aiReviewClient.review(toAiRequest(pr));

        saveReview(owner + "/" + repo, pr, ai);

        String reviewUrl = postToGithub(owner,repo,pr,ai);

        GitHubReviewResponse posted = reviewPublisher.publish(owner, repo, pr, ai);
        log.info("Posted GitHub review: {}", posted.htmlUrl());
        List<PullRequestReviewResponse.Finding> findings = ai.findings().stream().map(this::toFinding).toList();
        return new PullRequestReviewResponse(pr.number(),pr.title(),pr.url(),ai.summary(),ai.filesReviewed(),ai.truncated(),reviewUrl,findings);
    }
    private AiReviewRequest toAiRequest(PullRequestDetailsResponse pr){
        List<AiReviewRequest.FileDiff>files = pr.files().stream()
                .map(f->new AiReviewRequest.FileDiff(f.filename(),f.status().name(),f.patch()))
                .toList();
        return new AiReviewRequest(pr.title(),files);
    }

    private PullRequestReviewResponse.Finding toFinding(AiReviewResponse.Finding f){
        return new PullRequestReviewResponse.Finding(f.file(),f.line(),f.severity(),f.message(),f.suggestion());
    }

    private void saveReview(String repoFullname,PullRequestDetailsResponse  pr,AiReviewResponse ai){
        ReviewEntity review = new ReviewEntity(
                repoFullname,pr.number(),pr.title(),pr.url(),ai.summary(),ai.filesReviewed(),ai.truncated());
        for(AiReviewResponse.Finding f: ai.findings()) {
            review.addFinding(f.file(),f.line(),f.severity(),f.message(),f.suggestion());
        }
        reviewRepository.save(review);
            }



    @Transactional(readOnly = true)
    public List<StoredReviewResponse> getReviewHistory(String owner, String repo, int number){
        return reviewRepository
                .findByRepoFullNameAndPrNumberOrderByCreatedAtDesc(owner + "/" + repo,number)
                .stream()
                .map(this::toStoredReview)
                .toList();
    }

    private StoredReviewResponse toStoredReview(ReviewEntity r){
        List<PullRequestReviewResponse.Finding> findings = r.getFindings().stream()
                .map((f->new PullRequestReviewResponse.Finding(
                        f.getFile(),f.getLine(),f.getSeverity(),f.getMessage(),f.getSuggestion()
                ))).toList();
        return new StoredReviewResponse(r.getId(),r.getCreatedAt(),r.getSummary(),r.getFilesReviewed(),r.isTruncated(),findings);
    }

    private String postToGithub(String owner,String repo,PullRequestDetailsResponse pr,AiReviewResponse ai){
        try{return reviewPublisher.publish(owner,repo,pr,ai).htmlUrl();}
        catch(RestClientException | GitHubResourceNotFoundException e){
            log.warn("Could not post review to GitHub for {}/{}#{}: {}", owner, repo, pr.number(), e.getMessage());
            return null;
        }
    }
}

