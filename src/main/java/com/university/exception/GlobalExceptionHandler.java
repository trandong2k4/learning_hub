package com.university.exception;

import com.university.exception.students.HocVienChuaGanNganhException;
import com.university.exception.students.TaiLieuAccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.NestedExceptionUtils;

import java.net.URI;
import java.time.Instant;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler({ EntityNotFoundException.class, ResourceNotFoundException.class })
        public ProblemDetail handleNotFound(RuntimeException ex, WebRequest request) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
                pd.setTitle("Resource Not Found");
                pd.setType(URI.create("https://be-university.com/errors/not-found"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        @ExceptionHandler({ NotFoundException.class })
        public ProblemDetail handleNotFoundException(RuntimeException ex, WebRequest request) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
                pd.setTitle("Entity Not Found");
                pd.setType(URI.create("https://be-university.com/errors/not-found"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        @ExceptionHandler(HocVienChuaGanNganhException.class)
        public ProblemDetail handleHocVienChuaGanNganh(HocVienChuaGanNganhException ex, WebRequest request) {
                log.warn("Hoc vien chua duoc gan nganh: path={}", request.getDescription(false));
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
                pd.setTitle("Student Curriculum Conflict");
                pd.setType(URI.create("https://university.com/errors/student-curriculum-conflict"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        @ExceptionHandler(TaiLieuAccessDeniedException.class)
        public ProblemDetail handleTaiLieuAccessDenied(TaiLieuAccessDeniedException ex, WebRequest request) {
                log.warn("Truy cap tai lieu bi tu choi: path={}", request.getDescription(false));
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
                pd.setTitle("Tai Lieu Access Denied");
                pd.setType(URI.create("https://university.com/errors/tai-lieu-access-denied"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                String cause = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                log.error("HttpMessageNotReadable: path={}, cause={}", request.getDescription(false), cause);
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                "Dữ liệu gửi lên không đúng định dạng: " + cause);
                pd.setTitle("Bad Request");
                pd.setProperty("timestamp", java.time.Instant.now());
                pd.setInstance(java.net.URI.create(request.getDescription(false)));
                return createResponseEntity(pd, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {

                // Gom tất cả lỗi field thành một chuỗi để frontend hiển thị trực tiếp
                String detail = ex.getBindingResult().getFieldErrors().stream()
                                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                                .collect(Collectors.joining("; "));

                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                                detail.isBlank() ? "Dữ liệu không hợp lệ" : detail);
                pd.setTitle("Validation Error");
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));

                return createResponseEntity(pd, headers, status, request);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
                log.warn("Data integrity violation: path={}", request.getDescription(false), ex);
                String rootMessage = getMostSpecificMessage(ex);
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                                resolveDataIntegrityMessage(rootMessage));
                pd.setTitle("Conflict");
                pd.setType(URI.create("https://university.com/errors/conflict"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
                return pd;
        }

        private String getMostSpecificMessage(Throwable ex) {
                Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
                return root != null && root.getMessage() != null ? root.getMessage() : ex.getMessage();
        }

        private String resolveDataIntegrityMessage(String message) {
                if (message == null || message.isBlank()) {
                        return "Dữ liệu vi phạm ràng buộc trong cơ sở dữ liệu";
                }

                String lower = message.toLowerCase(Locale.ROOT);
                if (lower.contains("duplicate key") || lower.contains("unique constraint")) {
                        if (lower.contains("(email)")) {
                                return "Email đã tồn tại";
                        }
                        if (lower.contains("(cccd)")) {
                                return "CCCD đã tồn tại";
                        }
                        if (lower.contains("(username)") || lower.contains("username")) {
                                return "Tên đăng nhập đã tồn tại";
                        }
                        if (lower.contains("ma_nhan_vien")) {
                                return "Mã nhân viên đã tồn tại";
                        }
                        if (lower.contains("ma_hoc_vien")) {
                                return "Mã học viên đã tồn tại";
                        }
                        return "Dữ liệu bị trùng lặp";
                }

                if (lower.contains("null value in column")) {
                        if (lower.contains("\"users_id\"") && lower.contains("\"nhan_vien\"")) {
                                return "Nhân viên phải có tài khoản liên kết. Vui lòng chọn tài khoản có sẵn hoặc tạo tài khoản mới.";
                        }
                        return "Thiếu dữ liệu bắt buộc";
                }

                return "Dữ liệu vi phạm ràng buộc trong cơ sở dữ liệu";
        }

        @ExceptionHandler(SimpleMessageException.class)
        public ProblemDetail handleSimpleMessage(SimpleMessageException ex, WebRequest request) {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
                pd.setTitle("Bad Request");
                pd.setType(URI.create("https://university.com/errors/bad-request"));
                pd.setProperty("timestamp", Instant.now());
                pd.setInstance(URI.create(request.getDescription(false)));
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
                log.error("Unhandled exception: path={}", request.getDescription(false), ex);
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Da xay ra loi he thong");
                pd.setTitle("Internal Server Error");
                pd.setType(URI.create("https://university.com/errors/internal"));
                pd.setProperty("timestamp", Instant.now());
                return pd;
        }
}
