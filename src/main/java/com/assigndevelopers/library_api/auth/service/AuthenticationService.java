package com.assigndevelopers.library_api.auth.service;

import com.assigndevelopers.library_api.auth.model.AuthenticateRequest;
import com.assigndevelopers.library_api.auth.model.JWTResponse;
import com.assigndevelopers.library_api.auth.model.RegisterRequest;
import com.assigndevelopers.library_api.emailConfirmation.service.EmailConfirmationService;
import com.assigndevelopers.library_api.config.service.JWTService;
import com.assigndevelopers.library_api.customException.EntityNotFoundException;
import com.assigndevelopers.library_api.customException.GeneralException;
import com.assigndevelopers.library_api.token.entity.RefreshToken;
import com.assigndevelopers.library_api.token.service.RefreshTokenService;
import com.assigndevelopers.library_api.token.service.AccessTokenService;
import com.assigndevelopers.library_api.user.Role;
import com.assigndevelopers.library_api.user.entity.User;
import com.assigndevelopers.library_api.user.repository.UserRepository;
import com.assigndevelopers.library_api.user.service.FindUserByEmailOrPhoneOrIpAddress;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailConfirmationService emailConfirmationService;
    private final FindUserByEmailOrPhoneOrIpAddress findUserByEmailOrPhoneOrIpAddress;


    /**
     * Register/SignUp a New API Client/User
     */
    public void register(RegisterRequest registerRequest) {

        String userEmail = registerRequest.getEmail();

        if (userExists(userEmail)) {
            throw new GeneralException(userEmail, "User with already exist");
        }

        LocalDateTime localDateTime = LocalDateTime.now();

        // Create a New User
        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(userEmail)
                .ipAddress(registerRequest.getIpAddress())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                // By default, set Role to USER, if not provided
                .role(Role.USER)
                .createdAt(localDateTime)
                .updatedAt(localDateTime)
                .build();

        userRepository.save(user);

        emailConfirmationService.sendConfirmationMail(user);
    }

    /**
     * Resend Confirm Registered/SignUp Email
     */
    public void resendConfirmationMail(String userId) {

        var user = findUser(userId);

        // Is account not activated/verified?
        if (!user.isEnabled()) {
            emailConfirmationService.sendConfirmationMail(user);
        }
    }

    /**
     * Confirm Register/SignUp Email
     */
    public void confirmRegisteredEmail(String token) {
        emailConfirmationService.confirmRegisteredEmail(token);
    }

    /**
     * Authenticate/SignIn API Client/User
     * RETURN -> Generate New AccessToken(JWT) & New RefreshToken
     */
    public JWTResponse authenticate(AuthenticateRequest authenticateRequest) {
        String username = authenticateRequest.getUsername();
        String password = authenticateRequest.getPassword();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password
                )
        );

        var user = findUser(username);

        /*
         * Update Last Login-Time */
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // save new refreshToken
        // If JWT/ACCESS TOKEN expires, REFRESH_TOKEN is used to generate a new ACCESS_TOKEN
        RefreshToken rt = refreshTokenService.saveRefreshToken(user);

        // Response
        return accessTokenService.saveAccessToken(user, rt.getRefreshToken(), rt.getRefreshExpiration().toString());
    }

    /**
     * RETURN -> old RefreshToken & Generate New AccessToken(JWT)
     * REFRESH_TOKEN is used to generate a new ACCESS_TOKEN
     */
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {

        // System.out.println("Allowed Origin IP" + Utils.getLocalHostIp());

        // Get Authorization Token from Client Request payload Header
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String username;
        /*
         * Does Authorization Header has a Bearer Token
         * which start with "Bearer " from Client/User Request payload
         * */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        /*
         * Extract the Refresh Token from Client/User
         * Request payload (Authorization header -> "Bearer ") */
        refreshToken = authHeader.substring(7);

        username = jwtService.extractUserName(refreshToken);

        // Is username associated to RefreshToken
        if (username != null) {

            var user = findUser(username);

            // Is RefreshToken valid
            if (user.isEnabled() &&
                    jwtService.isTokenValid(refreshToken, user)) {

                // Generate new JWT/Access Token
                JWTResponse res = refreshTokenService.verifyRefreshToken(refreshToken)
                        .flatMap(
                                user1 -> refreshTokenService.findByRefreshToken(refreshToken)
                                        .map(rk ->
                                                accessTokenService.saveAccessToken(user1, rk.getRefreshToken(), rk.getRefreshExpiration().toString())
                                        )
                        )
                        .orElseThrow(
                                () -> new GeneralException(refreshToken, "Refresh token isn't associated to any account!")
                        );

                // Response
                new ObjectMapper()
                        .writeValue(response.getOutputStream(), res);
            }
        }

    }

    private User findUser(String userInput) {

        return findUserByEmailOrPhoneOrIpAddress.findBy(userInput)
                .orElseThrow(
                        () -> new EntityNotFoundException(User.class, "{username} ", userInput)
                );
    }

    public Boolean userExists(String userInput) {
        return findUserByEmailOrPhoneOrIpAddress.findBy(userInput).isPresent();
    }
}
