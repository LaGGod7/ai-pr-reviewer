package org.gd.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RepositoryNotFoundException.class)
    public ProblemDetail handleRepositoryNotFoundException(RepositoryNotFoundException ex) {
        ProblemDetail problem  = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Repository not found");
        return problem;
    }
}