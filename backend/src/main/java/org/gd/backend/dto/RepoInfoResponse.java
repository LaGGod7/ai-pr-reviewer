package org.gd.backend.dto;

public record RepoInfoResponse(
    String fullName,
    String defaultBranch,
    boolean isPrivate,
    String url){
}
