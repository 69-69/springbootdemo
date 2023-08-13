package com.assigndevelopers.library_api.config;

import com.assigndevelopers.library_api.config.service.JWTService;
import com.assigndevelopers.library_api.token.service.AccessTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTInterceptorAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final AccessTokenService accessTokenService;
    private final UserDetailsService userDetailsService;

    /**
     * This Intercept every Request Payload made by Client/user
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Get Authorization Token from Client Request payload Header
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userName;

        /*
         * Does Authorization Header has a Bearer Token
         * which start with "Bearer " from Client/User Request payload
         * */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Extract the Token from Client/User
         * Request payload (Authorization header -> "Bearer ") */
        jwtToken = authHeader.substring(7);

        userName = jwtService.extractUserName(jwtToken);

        // Allow Authorized Client/User only
        isUserAuthenticated(request, jwtToken, userName);

        // Pass it for filter execution
        filterChain.doFilter(request, response);
    }

    /**
     * Intercept user/client request
     */
    private void isUserAuthenticated(HttpServletRequest request, String jwtToken, String userName) {
        if (userName != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            // Check if Client/User exist in DB table
            UserDetails userDetails =
                    this.userDetailsService.loadUserByUsername(userName);

            /*
             * Check if Client:
             * 1- Email Address is Confirmed
             * 2- Authorization-Header JWT/Access Token is valid,
             * Then allow access to API endpoint */
            if (userDetails.isEnabled() &&
                    jwtService.isTokenValid(jwtToken, userDetails) &&
                    accessTokenService.isServerTokenUnExpired(jwtToken)) {
                // If valid, Create AuthToken/JwtToken(Access Token) from user info
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                /*
                 * Associate the new Token to user/client Details
                 * Build details out of client HTTP Request
                 * */
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Update the Security Context Holder
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authenticationToken);
            }
        }
    }
}
