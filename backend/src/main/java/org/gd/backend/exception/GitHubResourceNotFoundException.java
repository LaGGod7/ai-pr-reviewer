package org.gd.backend.exception;

public class GitHubResourceNotFoundException extends RuntimeException {
    public GitHubResourceNotFoundException(String path) {
        super("GitHub resource not found: " + path);
    }
}
