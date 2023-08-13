package com.assigndevelopers.library_api.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JWTResponse {

    /**
    * @apiNote
    * ACCESS_TOKEN has short lifespan.
    * REFRESH_TOKEN has long lifespan.
    * Hence, when ACCESS_TOKEN expires
    * REFRESH_TOKEN is used to get a new ACCESS_TOKEN
    * */
    private String accessToken;
    private String accessExpiration;
    private String refreshToken;
    private String refreshExpiration;
}
