package org.gd.backend.dto;


import java.util.List;

public record PullRequestReviewResponse(
        int number,
        String title,
        String url,
        String summary,
        int filesReviewed,
        boolean truncated,
        List<Finding> findings
) {
    public record Finding(String file, Integer line,String severity, String message, String suggestion) {
    }
}
