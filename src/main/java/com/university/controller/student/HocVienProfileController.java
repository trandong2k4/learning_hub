package com.university.controller.student;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.dto.request.student.HocVienProfileRequestDTO;
import com.university.dto.response.student.HocVienProfileResponseDTO;
import com.university.service.student.HocVienProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/student/profile")
@CrossOrigin
public class HocVienProfileController {

    private final HocVienProfileService service;

    public HocVienProfileController(HocVienProfileService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public String test() {
        return "API OK";
    }

    @GetMapping
    public HocVienProfileResponseDTO getProfile() {
        return service.getProfile();
    }

    @PutMapping
    public HocVienProfileResponseDTO updateProfile(
            @Valid @RequestBody HocVienProfileRequestDTO request) {
        return service.updateProfile(request);
    }
}
