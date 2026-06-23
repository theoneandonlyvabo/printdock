package com.printdock.printdock.service;

import com.printdock.printdock.dto.request.LoginRequest;
import com.printdock.printdock.dto.response.UserResponse;
import com.printdock.printdock.exception.InvalidInputException;
import com.printdock.printdock.exception.UserNotFoundException;
import com.printdock.printdock.model.user.Admin;
import com.printdock.printdock.model.user.Role;
import com.printdock.printdock.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AuthServiceImpl authService;

    @Test
    void login_validCredentials_returnsUserResponse() {
        Admin admin = new Admin(1L, "admin", "$2a$bcrypt");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password123", "$2a$bcrypt")).thenReturn(true);

        UserResponse resp = authService.login(new LoginRequest("admin", "password123"));

        assertThat(resp.getUsername()).isEqualTo("admin");
        assertThat(resp.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void login_userNotFound_throwsUserNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("unknown", "pass")))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void login_wrongPassword_throwsInvalidInputException() {
        Admin admin = new Admin(1L, "admin", "$2a$bcrypt");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongpass", "$2a$bcrypt")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin", "wrongpass")))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("password");
    }
}
