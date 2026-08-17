package com.lucke.luckeride.auth.service;

import com.lucke.luckeride.auth.dto.AuthResponse;
import com.lucke.luckeride.auth.dto.LoginRequest;
import com.lucke.luckeride.auth.entity.RefreshToken;
import com.lucke.luckeride.auth.security.JwtService;
import com.lucke.luckeride.user.entity.User;
import com.lucke.luckeride.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow();

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    @Transactional
    public AuthResponse refresh(String rawRefreshToken) {

        RefreshToken currentToken =
                refreshTokenService.validateForRefresh(
                        rawRefreshToken
                );

        User user = currentToken.getUser();

        String accessToken =
                jwtService.generateAccessToken(user);

        String newRefreshToken =
                refreshTokenService.rotateRefreshToken(
                        currentToken
                );

        return new AuthResponse(
                accessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }
}