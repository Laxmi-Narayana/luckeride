package com.lucke.luckeride.auth.service;

import com.lucke.luckeride.auth.entity.RefreshToken;
import com.lucke.luckeride.auth.exception.InvalidRefreshTokenException;
import com.lucke.luckeride.auth.repository.RefreshTokenRepository;
import com.lucke.luckeride.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final Logger log =
            LoggerFactory.getLogger(RefreshTokenService.class);

    private static final int TOKEN_BYTES = 32;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Duration refreshTokenExpiration;
    private final RefreshTokenRevocationService revocationService;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${app.jwt.refresh-token-expiration}")
            Duration refreshTokenExpiration,
            RefreshTokenRevocationService revocationService
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.revocationService = revocationService;
    }

    @Transactional
    public String createRefreshToken(User user) {
        return createRefreshToken(user, UUID.randomUUID());
    }

    @Transactional
    public String rotateRefreshToken(RefreshToken currentToken) {

        currentToken.revoke();

        return createRefreshToken(
                currentToken.getUser(),
                currentToken.getFamilyId()
        );
    }

    @Transactional
    public RefreshToken validateForRefresh(String rawToken) {

        String tokenHash = hash(rawToken);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Refresh token not found"
                        )
                );

        if (refreshToken.isRevoked()) {

            revocationService.revokeFamily(
                    refreshToken.getFamilyId()
            );

            throw new InvalidRefreshTokenException(
                    "Refresh token reuse detected"
            );
        }

        if (refreshToken.isExpired()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    private String createRefreshToken(
            User user,
            UUID familyId
    ) {

        byte[] randomBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hash(rawToken);

        RefreshToken refreshToken = new RefreshToken(
                user,
                tokenHash,
                familyId,
                Instant.now().plus(refreshTokenExpiration)
        );

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    private String hash(String value) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hex =
                    new StringBuilder(hash.length * 2);

            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm unavailable",
                    e
            );
        }
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {

        String tokenHash = hash(rawToken);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Refresh token not found"
                        )
                );

        if (refreshToken.isRevoked()) {
            return;
        }

        refreshToken.revoke();
    }
}