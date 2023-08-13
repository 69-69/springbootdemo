package com.assigndevelopers.library_api.auth;

import com.assigndevelopers.library_api.auth.model.*;
import com.assigndevelopers.library_api.auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid
            @RequestBody
            RegisterRequest registerRequest) {

        authenticationService.register(registerRequest);

        return ResponseEntity.ok().build();
    }


    /**
     * <a href="http://hostname/api/v1/auth/register/confirm_email?token=eyJhbGciOiJ">...</a>
     */
    @GetMapping("/register/confirm_email")
    public void confirmRegistration(@RequestParam String token) {
        authenticationService.confirmRegisteredEmail(token);
    }

    /*
     * /api/v1/auth/resend_confirm_email?param=admin@gmail.com */
    @PostMapping("/resend_confirm_email")
    public ResponseEntity<String> resendConfirmMail(@RequestParam String email) {
        authenticationService.resendConfirmationMail(email);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTResponse> auth(
            @Valid
            @RequestBody
            AuthenticateRequest authenticateRequest) {

        return ResponseEntity.ok(authenticationService.authenticate(authenticateRequest));
    }

    /*
     * REFRESH_TOKEN is saved in the Database for reference CALL.
     * So when ACCESS_TOKEN (JWT_Token) expires,
     * REFRESH_TOKEN is used to get a new ACCESS_TOKEN.*/
    @PostMapping("/refresh_token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // Client/User IP Utils.getClientIp(httpRequest);

        authenticationService.refreshToken(request, response);
    }

    /*
     * @Param: user email/ip/phoneNumber
     * /api/v1/auth/user_exist?param=admin@gmail.com */
    @PostMapping("/user_exist")
    public ResponseEntity<Boolean> userExist(@RequestParam String param) {

        return ResponseEntity.ok(authenticationService.userExists(param));
    }
}
