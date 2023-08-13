package com.assigndevelopers.library_api.token.repository;


import com.assigndevelopers.library_api.token.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {

    // Get All Tokens from the Token TABLE
    /* * @Query("SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id " +
            "WHERE u.id = :userId AND (t.expired = FALSE OR t.revoked = FALSE )"
    )
    List<Token> finAllValidTokensByUser(Integer userId);*/

    Optional<AccessToken> findByAccessToken(String token);

    Optional<AccessToken> findByUserId(Integer userId);

    /// @Modifying
    // int deleteByUser(User user);
}
