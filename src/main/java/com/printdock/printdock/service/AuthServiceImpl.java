package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.LoginRequest;
import com.printdock.printdock.dto.response.UserResponse;
import com.printdock.printdock.exception.InvalidInputException;
import com.printdock.printdock.exception.UserNotFoundException;
import com.printdock.printdock.model.user.User;
import com.printdock.printdock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User tidak ditemukan: " + request.getUsername()));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new InvalidInputException("Username atau password salah");
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
