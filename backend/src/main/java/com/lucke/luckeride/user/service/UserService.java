package com.lucke.luckeride.user.service;

import com.lucke.luckeride.common.exception.ResourceConflictException;
import com.lucke.luckeride.user.dto.CreateUserRequest;
import com.lucke.luckeride.user.entity.User;
import com.lucke.luckeride.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(CreateUserRequest request) {

        String email = request.email().trim().toLowerCase();
        String phoneNumber = request.phoneNumber().trim();

        if (userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email is already registered");
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ResourceConflictException("Phone number is already registered");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = User.create(
                email,
                passwordHash,
                request.firstName().trim(),
                request.lastName().trim(),
                phoneNumber
        );

        return userRepository.save(user);
    }
}