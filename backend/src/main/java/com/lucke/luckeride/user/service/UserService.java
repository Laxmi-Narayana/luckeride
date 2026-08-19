package com.lucke.luckeride.user.service;

import com.lucke.luckeride.auth.service.RefreshTokenService;
import com.lucke.luckeride.common.exception.ResourceConflictException;
import com.lucke.luckeride.user.dto.CreateUserRequest;
import com.lucke.luckeride.user.dto.ChangePasswordRequest;
import com.lucke.luckeride.user.dto.UpdateProfileRequest;
import com.lucke.luckeride.user.entity.User;
import com.lucke.luckeride.common.exception.InvalidCurrentPasswordException;
import com.lucke.luckeride.common.exception.SamePasswordException;
import com.lucke.luckeride.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
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

    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User with id " + userId + " not found")
        );
    }

    @Transactional
    public User updateProfile(UUID userId,
                              UpdateProfileRequest request) {
        User user = getUserById(userId);
        user.updateProfile(
                request.firstName(),
                request.lastName(),
                request.phoneNumber()
        );
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(UUID userId,
                               ChangePasswordRequest request) {
        User user = getUserById(userId);
        if(!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }

        if(passwordEncoder.matches(
                request.newPassword(),
                user.getPasswordHash()
        )) {
            throw new SamePasswordException();
        }

        user.changePassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        userRepository.save(user);

        refreshTokenService.revokeAllForUser(
                user.getId()
        );
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}