package org.gd.backend.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubReviewResponse(long id,
                                   @JsonProperty("html_url") String htmlUrl) {
}
