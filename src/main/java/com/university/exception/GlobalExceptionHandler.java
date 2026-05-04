package com.university.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler({ EntityNotFoundException.class, ResourceNotFoundException.class })
        public ProblemDetail handleNotFound(RuntimeException ex, WebRequest request) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
                pd.setTitle("Resource Not Found");
                pd.setType(URI.create("https://be-university.com/errors/not-found"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        @SuppressWarnings("null")
        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
                pd.setTitle("Validation Error");

                Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                                .collect(Collectors.toMap(
                                                fieldError -> fieldError.getField(),
                                                fieldError -> fieldError.getDefaultMessage(),
                                                (msg1, msg2) -> msg1 + "; " + msg2));

                pd.setProperty("errors", errors);
                pd.setInstance(URI.create(request.getDescription(false)));

                return createResponseEntity(pd, headers, status, request);
        }

        @ExceptionHandler(SimpleMessageException.class)
        public ProblemDetail handleUnauthorized(SimpleMessageException ex) {
                ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
                pd.setTitle("Unauthorized");
                pd.setDetail(ex.getMessage());
                pd.setType(URI.create("https://university.com/errors/unauthorized"));
                return pd;
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ProblemDetail handleBadRequest(IllegalArgumentException ex, WebRequest request) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
                pd.setTitle("Bad Request");
                pd.setType(URI.create("https://university.com/errors/bad-request"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        @ExceptionHandler(IllegalStateException.class)
        public ProblemDetail handleConflict(IllegalStateException ex, WebRequest request) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
                pd.setTitle("Conflict");
                pd.setType(URI.create("https://university.com/errors/conflict"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        @SuppressWarnings("null")
        @Override
        protected ResponseEntity<Object> handleNoHandlerFoundException(
                        org.springframework.web.servlet.NoHandlerFoundException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                "Khong tim thay tai nguyen: " + ex.getRequestURL());
                pd.setTitle("Resource Not Found");
                return createResponseEntity(pd, headers, status, request);
        }

        @ExceptionHandler(Exception.class)
        public ProblemDetail handleAllExceptions(Exception ex, WebRequest request) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Da xay ra loi he thong");
                pd.setTitle("Internal Server Error");
                pd.setType(URI.create("https://university.com/errors/internal"));
                pd.setProperty("timestamp", Instant.now());
                return pd;
        }
}
