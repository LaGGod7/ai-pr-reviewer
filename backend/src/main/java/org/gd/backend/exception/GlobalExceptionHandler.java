package org.gd.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(GitHubResourceNotFoundException.class)
    public ProblemDetail handleRepositoryNotFoundException(GitHubResourceNotFoundException ex) {
        ProblemDetail problem  = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource not found");
        return problem;
    }
    @ExceptionHandler(AiServiceUnavailableException.class)
    public ProblemDetail handleAiServiceUnavailableException(AiServiceUnavailableException ex) {
        ProblemDetail problem =  ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        problem.setTitle("AI review unavailable");
        log.warn("AI service unavailable", ex);
        return problem;
    }
}