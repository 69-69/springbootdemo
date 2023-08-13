package com.assigndevelopers.library_api.token.repository;

import com.assigndevelopers.library_api.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(Integer userId);

    /// @Modifying
    //int deleteByUser(User user);
}
