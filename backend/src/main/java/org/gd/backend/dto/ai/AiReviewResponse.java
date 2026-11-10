package org.gd.backend.dto.ai;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record  AiReviewResponse(
        String summary,
        List<Finding> findings,
        @JsonProperty("files_reviewed") int filesReviewed,
        boolean truncated
) {
    public record Finding(
            String file,
            Integer line,
            String severity,
            String message,
            String suggestion
    ){}
}
