package org.gd.backend.dto.github;

public record RepoInfoResponse(
    String fullName,
    String defaultBranch,
    boolean isPrivate,
    String url){
}
