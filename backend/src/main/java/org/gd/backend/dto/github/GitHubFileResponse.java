package org.gd.backend.dto.github;

public record GitHubFileResponse(
        String filename,
        String status,
        int additions,
        int deletions,
        String patch
) {

}
