package com.lucke.luckeride.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        log.info(
                "JWT filter: {} {} | Authorization present: {}",
                request.getMethod(),
                request.getRequestURI(),
                authorizationHeader != null
        );

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            log.info("JWT filter: no Bearer token");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            log.info("JWT filter: token is INVALID");

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtService.extractSubject(token);
        String role = jwtService.extractRole(token);

        UUID userId = jwtService.extractUserId(token);

        AuthenticatedUser principal =
                new AuthenticatedUser(
                        jwtService.extractUserId(token),
                        email,
                        role
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        )
                );
        authentication.setDetails(userId);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        log.info(
                "JWT filter: authentication set | user={} | authorities={} | authenticated={}",
                authentication.getName(),
                authentication.getAuthorities(),
                authentication.isAuthenticated()
        );

        filterChain.doFilter(request, response);

        log.info(
                "JWT filter: after chain | {} {} | authentication={}",
                request.getMethod(),
                request.getRequestURI(),
                SecurityContextHolder.getContext().getAuthentication()
        );
    }
}