package com.kunal.taskmanager.controller;

import com.kunal.taskmanager.common.APIResponse;
import com.kunal.taskmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public APIResponse<String> register(@RequestBody Map<String, String> request){
       return authService.register(request.get("username"), request.get("password"));
//        return "User Registered successfully";
    }

    @PostMapping("/login")
    public APIResponse<String> login(@RequestBody Map<String, String> request){
        return authService.login(request.get("username"), request.get("password"));
    }
}
