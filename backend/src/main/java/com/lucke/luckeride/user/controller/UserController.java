package com.lucke.luckeride.user.controller;

import com.lucke.luckeride.auth.security.AuthenticatedUser;
import com.lucke.luckeride.user.dto.ChangePasswordRequest;
import com.lucke.luckeride.user.dto.CreateUserRequest;
import com.lucke.luckeride.user.dto.UpdateProfileRequest;
import com.lucke.luckeride.user.dto.UserResponse;
import com.lucke.luckeride.user.entity.User;
import com.lucke.luckeride.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser(
            Authentication authentication
    ) {
        AuthenticatedUser user = getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                Map.of(
                        "email", user.email(),
                        "role", "ROLE_" + user.role()
                )
        );
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        User user = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(user));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<UserResponse> getMyProfile(
            Authentication authentication
    ) {
        User user = userService.getUserById(
                getCurrentUserId(authentication)
        );

        return ResponseEntity.ok(toResponse(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        User user = userService.updateProfile(
                getCurrentUserId(authentication),
                request
        );

        return ResponseEntity.ok(toResponse(user));
    }

    @PostMapping("/me/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(
                getCurrentUserId(authentication),
                request
        );
    }

    private UUID getCurrentUserId(
            Authentication authentication
    ) {
        return getAuthenticatedUser(authentication).userId();
    }

    private AuthenticatedUser getAuthenticatedUser(
            Authentication authentication
    ) {
        return (AuthenticatedUser) authentication.getPrincipal();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}