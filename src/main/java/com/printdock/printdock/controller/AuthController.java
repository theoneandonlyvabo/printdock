package com.printdock.printdock.controller;

import com.printdock.printdock.dto.ApiResponse;
import com.printdock.printdock.dto.request.LoginRequest;
import com.printdock.printdock.dto.response.UserResponse;
import com.printdock.printdock.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", authService.login(request)));
    }
}
