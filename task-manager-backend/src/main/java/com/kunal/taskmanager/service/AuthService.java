package com.kunal.taskmanager.service;

import com.kunal.taskmanager.common.APIResponse;
import com.kunal.taskmanager.entity.User;
import com.kunal.taskmanager.repository.UserRepository;
import com.kunal.taskmanager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public APIResponse<String> register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return new APIResponse<>(true, "User Registered successfully", null);
    }

    public APIResponse<String> login(String username, String password){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        return new APIResponse<>(true, "User Logged In successfully", jwtUtil.generateToken(username));
    }
}
