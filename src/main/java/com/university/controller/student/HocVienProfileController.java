// package com.university.controller.student;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.university.service.student.HocVienProfileService;
// import java.util.UUID;
// import org.springframework.web.bind.annotation.*;

// import com.university.dto.request.student.HocVienProfileRequestDTO;
// import com.university.dto.response.student.HocVienProfileResponseDTO;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/student/profile")
// @CrossOrigin
// public class HocVienProfileController {

//     private final HocVienProfileService service;

//     public HocVienProfileController(HocVienProfileService service) {
//         this.service = service;
//     }
    
//     // @GetMapping("/test")
//     // public String test(){
//     //      return "API OK";
//     // }

//     @GetMapping("/{userId}")
//     public HocVienProfileResponseDTO getProfile(@PathVariable UUID userId) {
//         return service.getProfile(userId);
//     }

//     @PutMapping("/{userId}")
//     public HocVienProfileResponseDTO updateProfile(
//             @PathVariable UUID userId,
//             @Valid @RequestBody HocVienProfileRequestDTO request) {

//         return service.updateProfile(userId, request);
//     }
// }