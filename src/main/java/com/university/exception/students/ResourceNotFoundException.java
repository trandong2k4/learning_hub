package com.university.exception.students;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, UUID id) {
        super(resource + " không tồn tại với id: " + id);
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}