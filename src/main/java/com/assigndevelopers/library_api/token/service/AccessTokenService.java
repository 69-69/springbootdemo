package com.assigndevelopers.library_api.token.service;

import com.assigndevelopers.library_api.auth.model.JWTResponse;
import com.assigndevelopers.library_api.config.service.JWTService;
import com.assigndevelopers.library_api.emailConfirmation.service.EmailSenderService;
import com.assigndevelopers.library_api.token.TokenType;
import com.assigndevelopers.library_api.token.entity.AccessToken;
import com.assigndevelopers.library_api.token.repository.AccessTokenRepository;
import com.assigndevelopers.library_api.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class AccessTokenService {

    private final JWTService jwtService;
    private final EmailSenderService emailSenderService;
    private final AccessTokenRepository accessTokenRepository;

    @Autowired
    public AccessTokenService(JWTService jwtService, EmailSenderService emailSenderService, AccessTokenRepository accessTokenRepository) {
        this.jwtService = jwtService;
        this.emailSenderService = emailSenderService;
        this.accessTokenRepository = accessTokenRepository;
    }

    /**
     * @apiNote : Check if Token from Database is unexpired */
    public boolean isServerTokenUnExpired(String token) {
        return findByAccessToken(token)
                .map(t -> !t.getAccessExpiration().before(new Date()))
                .orElse(false);
        /* * boolean isTokenValid = accessTokenRepository.findByToken(refreshToken)
                    .map(t -> t.isExpired() && t.isRevoked())
                    .orElse(false);*/
    }

    public Optional<AccessToken> findByAccessToken(String token) {
        return accessTokenRepository.findByAccessToken(token);
    }

    public void deleteByToken(AccessToken storedToken) {
        accessTokenRepository.delete(storedToken);
    }

    // save new accessToken(JWT)
    public JWTResponse saveAccessToken(User user, String refreshToken, String refreshExpiry) {
        // revoke old tokens
        revokeOldAccessToken(user);

        // Generate JWT-Token(Access Token) for User
        Map<String, Object> jwt = jwtService.generateJWTToken(user);
        String accessToken = jwt.get("jwtAccessToken").toString();
        Date accessExpiration = (Date) jwt.get("jwtAccessExpiration");

        AccessToken token = AccessToken.builder()
                .user(user)
                .accessToken(accessToken)
                .tokenType(TokenType.BEARER)
                .accessExpiration(accessExpiration)
                .build();

        accessTokenRepository.save(token);
        emailSenderService.accessTokenNotify(user.getEmail());

        //  Response
        return JWTResponse
                .builder()
                .accessToken(accessToken)
                // Return old RefreshToken
                .refreshToken(refreshToken)
                .refreshExpiration(refreshExpiry)
                .accessExpiration(accessExpiration.toString())
                .build();
    }

    private void revokeOldAccessToken(User user) {
        Optional<AccessToken> savedToken =
                accessTokenRepository.findByUserId(user.getId());

        if (savedToken.isEmpty()) return;

        deleteByToken(savedToken.get());
    }
}
