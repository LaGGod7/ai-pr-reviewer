package org.gd.backend.exception;

public class RepositoryNotFoundException extends RuntimeException {
    public RepositoryNotFoundException(String owner, String repo) {
        super("Repository " + owner + "/" + repo + " not found");
    }
}
