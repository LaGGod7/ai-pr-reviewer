package org.gd.backend.controller;

import org.gd.backend.dto.PullRequestReviewResponse;
import org.gd.backend.dto.StoredReviewResponse;
import org.gd.backend.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{owner}/{repo}/pulls/{number}")
    public PullRequestReviewResponse reviewPullRequest(@PathVariable String owner, @PathVariable String repo, @PathVariable int number){
        return reviewService.reviewPullRequest(owner,repo,number);
    }
    @GetMapping("/{owner}/{repo}/pulls/{number}")
    public List<StoredReviewResponse> getReviewHistory(@PathVariable String owner,
                                                       @PathVariable String repo,
                                                       @PathVariable int number) {
        return reviewService.getReviewHistory(owner, repo, number);
    }
}
