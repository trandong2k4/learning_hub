package com.university.exception;

import org.springframework.http.*;
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

        // 1. EntityNotFound / ResourceNotFound → 404
        @ExceptionHandler({ EntityNotFoundException.class, ResourceNotFoundException.class })
        public ProblemDetail handleNotFound(RuntimeException ex, WebRequest request) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
                pd.setTitle("Resource Not Found");
                pd.setType(URI.create("https://be-university.com/errors/not-found")); // optional, link docs
                pd.setProperty("timestamp", Instant.now()); // custom field
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        // 2. Validation errors (400) - dùng ProblemDetail chuẩn
        @SuppressWarnings("null")
        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                "Validation failed");
                pd.setTitle("Validation Error");

                Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                                .collect(Collectors.toMap(
                                                fieldError -> fieldError.getField(),
                                                fieldError -> fieldError.getDefaultMessage(),
                                                (msg1, msg2) -> msg1 + "; " + msg2));

                pd.setProperty("errors", errors); // thêm field tùy ý
                pd.setInstance(URI.create(request.getDescription(false)));

                return createResponseEntity(pd, headers, status, request);
        }

        // 3. Unauthorized / SimpleMessageException (401)
        @ExceptionHandler(SimpleMessageException.class)
        public ProblemDetail handleUnauthorized(SimpleMessageException ex) {
                ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
                pd.setTitle("Unauthorized");
                pd.setDetail(ex.getMessage());
                pd.setType(URI.create("https://university.com/errors/unauthorized"));
                return pd;
        }

        // 4. NoResourceFoundException (404 cho static file)
        @SuppressWarnings("null")
        @Override
        protected ResponseEntity<Object> handleNoHandlerFoundException(
                        org.springframework.web.servlet.NoHandlerFoundException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy tài nguyên: " + ex.getRequestURL());
                pd.setTitle("Resource Not Found");
                return createResponseEntity(pd, headers, status, request);
        }

        // 5. Catch-all (500) - chỉ log, không lộ stacktrace
        @ExceptionHandler(Exception.class)
        public ProblemDetail handleAllExceptions(Exception ex, WebRequest request) {
                // log ở đây nếu cần (logger.error("Unexpected error", ex))

                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Đã xảy ra lỗi hệ thống");
                pd.setTitle("Internal Server Error");
                pd.setType(URI.create("https://university.com/errors/internal"));
                pd.setProperty("timestamp", Instant.now());
                // KHÔNG thêm ex.getMessage() hoặc stacktrace vào response (security)

                return pd;
        }
}