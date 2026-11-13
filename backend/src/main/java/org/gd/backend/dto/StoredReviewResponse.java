package org.gd.backend.dto;

import java.time.Instant;
import java.util.List;

public record StoredReviewResponse(Long id,
                                   Instant createdAt,
                                   String summary,
                                   int filesReviewed,
                                   boolean truncated,
                                   List<PullRequestReviewResponse.Finding> findings) {
}
