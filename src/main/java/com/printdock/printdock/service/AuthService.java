package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.LoginRequest;
import com.printdock.printdock.dto.response.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}
