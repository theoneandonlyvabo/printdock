package com.printdock.printdock.config;

import com.printdock.printdock.model.user.Admin;
import com.printdock.printdock.model.user.Role;
import com.printdock.printdock.model.user.Staff;
import com.printdock.printdock.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seedUsers() {
        if (!userRepository.existsByUsername("vano")) {
            Staff vano = new Staff();
            vano.setUsername("vano");
            vano.setPassword(passwordEncoder.encode("12345678"));
            vano.setRole(Role.STAFF);
            userRepository.save(vano);
        }
    }
}
