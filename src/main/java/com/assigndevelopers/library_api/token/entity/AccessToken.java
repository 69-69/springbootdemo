package com.assigndevelopers.library_api.token.entity;

import com.assigndevelopers.library_api.token.TokenType;
import com.assigndevelopers.library_api.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "access_token")
public class AccessToken {

    @Id
    @GeneratedValue
    private Integer id;

    private String accessToken;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private Date accessExpiration;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
