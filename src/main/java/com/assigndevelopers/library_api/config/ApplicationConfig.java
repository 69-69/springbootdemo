package com.assigndevelopers.library_api.config;

import com.assigndevelopers.library_api.customException.EntityNotFoundException;
import com.assigndevelopers.library_api.user.entity.User;
import com.assigndevelopers.library_api.user.service.FindUserByEmailOrPhoneOrIpAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final FindUserByEmailOrPhoneOrIpAddress findUserByEmailOrPhoneOrIpAddress;

    @Bean
    public UserDetailsService userDetailsService() {
        return userInput -> findUserByEmailOrPhoneOrIpAddress.findBy(userInput)
                .orElseThrow(
                        () -> new EntityNotFoundException(User.class, "{username} ", userInput)
                );

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // Responsible to manage the Authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
