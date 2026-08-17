package com.lucke.luckeride.auth.service;

import com.lucke.luckeride.auth.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenRevocationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    RefreshTokenRevocationService.class
            );

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenRevocationService(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeFamily(UUID familyId) {

        int revokedCount =
                refreshTokenRepository.revokeFamily(
                        familyId,
                        Instant.now()
                );

        log.warn(
                "Revoked {} refresh token(s) in family {} after reuse detection",
                revokedCount,
                familyId
        );
    }
}