package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/test")
@RequirePermission("ADMIN_DASHBOARD_ADMIN_VIEW")
public class AdminController {

    @GetMapping
    public String testApi() {
        return "Hello, I`m ADMIN! You have access";
    }
}