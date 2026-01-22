package com.example.authapp.controller;

import com.example.authapp.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping
    public String dashboard(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return "Welcome " + user.getEmail();
    }
}
