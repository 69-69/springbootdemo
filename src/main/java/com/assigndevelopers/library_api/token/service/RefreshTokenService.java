package com.assigndevelopers.library_api.token.service;

import com.assigndevelopers.library_api.config.service.JWTService;
import com.assigndevelopers.library_api.customException.GeneralException;
import com.assigndevelopers.library_api.token.TokenType;
import com.assigndevelopers.library_api.token.entity.RefreshToken;
import com.assigndevelopers.library_api.token.repository.RefreshTokenRepository;
import com.assigndevelopers.library_api.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final JWTService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(JWTService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Optional<RefreshToken> findByRefreshToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    // Is Refresh Token Expired
    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getRefreshExpiration().before(new Date())) {
            // RefreshToken expired, so delete from DB
            refreshTokenRepository.delete(token);

            throw new GeneralException(token.getRefreshToken(), "Refresh token expired. Please make a new SignIn request");
        }

        return token;
    }

    public RefreshToken saveRefreshToken(User user) {

        // If user has unExpired RefreshToken, throw an exception
        Optional<RefreshToken> rk = refreshTokenRepository.findByUserId(user.getId());
        if(rk.isPresent() && rk.get().getRefreshExpiration().after(new Date())){
            throw new GeneralException(rk.get().getRefreshToken(), "Refresh token is not expired; " +
                    "use the [Refresh token] in [Authorization header] for a new [access token]");
        }

        Map<String, Object> tk = jwtService.generateRefreshToken(user);

        String token = tk.get("jwtRefreshToken").toString();
        Date refreshExpiration = (Date) tk.get("jwtRefreshExpiration");

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setTokenType(TokenType.BEARER);
        refreshToken.setRefreshExpiration(refreshExpiration);
        refreshToken.setRefreshToken(token);

        // Return New RefreshToken
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<User> verifyRefreshToken(String refreshToken) {

        return findByRefreshToken(refreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                /*.map(user -> JWTResponse
                        .builder()
                        .accessToken(jwt.get("jwtAccessToken").toString())
                        .refreshToken(refreshToken)
                        .accessExpiration(jwt.get("jwtAccessExpiration").toString())
                        .build())
                .orElseThrow(
                        () -> new GeneralException(refreshToken, "Refresh token isn't associated to any account!")
                )*/;
    }
}
