package com.kunal.taskmanager.controller;

import com.kunal.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> request){
        authService.register(request.get("username"), request.get("password"));
        return "User Registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> request){
        return authService.login(request.get("username"), request.get("password"));
    }
}
