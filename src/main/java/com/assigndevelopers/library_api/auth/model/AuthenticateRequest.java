package com.assigndevelopers.library_api.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateRequest {

    /**
     * @apiNote : username -> Email or Client_IP
     */
    private String username;
    private String password;
}
