package com.assigndevelopers.library_api.config.service;

import com.assigndevelopers.library_api.token.service.AccessTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final AccessTokenService accessTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        // Get Authorization Token from Client Request payload Header
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        /*
         * Does Authorization Header has a Bearer Token
         * which start with "Bearer " from Client/User Request payload
         * */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        /*
         * Extract the Access/Refresh Token from Client/User
         * Request payload (Authorization header -> "Bearer ") */
        jwtToken = authHeader.substring(7);

        var storedToken = accessTokenService.findByAccessToken(jwtToken)
                .orElse(null);

        /*
         * If client JWT Token (Access Token) from Authorization header
         * is valid and same as the one stored in the DB Table,
         * then delete it
         */
        if (storedToken != null) {
            accessTokenService.deleteByToken(storedToken);
        }
    }
}
