package org.gd.backend.dto.ai;

import java.util.List;

public record AiReviewRequest(String title, List<FileDiff> files) {

    public record FileDiff(String filename, String status, String patch) {}
}