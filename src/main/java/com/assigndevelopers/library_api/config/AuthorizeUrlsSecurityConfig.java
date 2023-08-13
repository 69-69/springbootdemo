package com.assigndevelopers.library_api.config;

import com.assigndevelopers.library_api.customException.CustomAccessDeniedHandler;
import com.assigndevelopers.library_api.customException.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.assigndevelopers.library_api.user.Permission.*;
import static com.assigndevelopers.library_api.user.Role.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthorizeUrlsSecurityConfig {

    private final JWTInterceptorAuthFilter jwtInterceptorAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final LogoutHandler logoutHandler;

    // Responsible for configuring All HTTP Security of This Application
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cor -> {
                    try {
                        cor.init(httpSecurity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })

                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(
                        (auth) ->
                                auth
                                        /* Unrestricted Endpoints: (Login, Signup) */
                                               .requestMatchers(
                                                "/api/v1/auth/**",
                                                // "/register/confirm_email/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/v2/api-docs",
                                                "/v3/api-docs",
                                                "/v3/api-docs/**",
                                                "/swagger-resources",
                                                "/swagger-resources/**",
                                                "/configuration/ui",
                                                "/configuration/ui/**",
                                                "/swagger-resources/security",
                                                "/webjars/**"
                                        )
                                        .permitAll()
                                        /* Restricted Endpoints: (Library APIs) */
                                        // .requestMatchers("/api/v1/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER", "ROLE_MANGER")
                                        // ROLE
                                        .requestMatchers("/api/v1/users/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                        // PERMISSION
                                        .requestMatchers(GET,"/api/v1/users/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                        .requestMatchers(POST,"/api/v1/users/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                        .requestMatchers(DELETE,"/api/v1/users/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                        .requestMatchers(PUT,"/api/v1/users/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                        // ROLE
                                        .requestMatchers("/api/v1/libraries/**").hasRole(ADMIN.name())
                                        // PERMISSION
                                        .requestMatchers(GET,"/api/v1/libraries/**").hasAuthority(ADMIN_READ.name())
                                        .requestMatchers(POST,"/api/v1/libraries/**").hasAuthority(ADMIN_CREATE.name())
                                        .requestMatchers(DELETE,"/api/v1/libraries/**").hasAuthority(ADMIN_DELETE.name())
                                        .requestMatchers(PUT,"/api/v1/libraries/**").hasAuthority(ADMIN_UPDATE.name())
                                        .anyRequest()
                                        .authenticated()
                )

                .sessionManagement(
                        (session) ->
                                session
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                /*.sessionConcurrency(
                                        (sessionConcurrency) ->
                                                sessionConcurrency
                                                        .maximumSessions(1)
                                                        .expiredUrl("/login?expired")
                                )*/
                )

                .authenticationProvider(authenticationProvider)

                .addFilterBefore(jwtInterceptorAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logoutConfigurer -> {
                    logoutConfigurer.addLogoutHandler(logoutHandler);

                    logoutConfigurer.logoutUrl("/api/v1/auth/logout");

                    logoutConfigurer.addLogoutHandler(logoutHandler);

                    // After Client logout, Clear previous security context, so client can't access the API
                    logoutConfigurer.logoutSuccessHandler(
                            (request, response, authentication) -> SecurityContextHolder.clearContext()
                    );
                })

                // Deny Access to secured APIs Endpoints, if JWT Token expires
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        return httpSecurity.build();
    }

}
