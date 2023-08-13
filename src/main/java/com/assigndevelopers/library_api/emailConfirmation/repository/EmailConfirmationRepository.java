package com.assigndevelopers.library_api.emailConfirmation.repository;

import com.assigndevelopers.library_api.emailConfirmation.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, Integer> {
    Optional<EmailConfirmation> findEmailConfirmationByConfirmationToken(String token);
}
