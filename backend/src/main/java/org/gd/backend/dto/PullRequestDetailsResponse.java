package org.gd.backend.dto;

import java.util.List;

public record PullRequestDetailsResponse(
        int number,
        String title,
        String author,
        String sourceBranch,
        String targetBranch,
        String headSha,
        String url,
        List<ChangedFile> files
){
    public record ChangedFile(
            String filename,
            FileStatus status,
            int additions,
            int deletions,
            String patch
    ) {}
}
