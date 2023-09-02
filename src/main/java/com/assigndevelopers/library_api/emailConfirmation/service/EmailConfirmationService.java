package com.assigndevelopers.library_api.emailConfirmation.service;

import com.assigndevelopers.library_api.config.service.JWTService;
import com.assigndevelopers.library_api.customException.GeneralException;
import com.assigndevelopers.library_api.emailConfirmation.EmailConfirmation;
import com.assigndevelopers.library_api.emailConfirmation.repository.EmailConfirmationRepository;
import com.assigndevelopers.library_api.user.entity.User;
import com.assigndevelopers.library_api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailConfirmationService {
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final EmailConfirmationRepository emailConfirmationRepository;

    @Autowired
    public EmailConfirmationService(JWTService jwtService, UserRepository userRepository, EmailSenderService emailSenderService, EmailConfirmationRepository emailConfirmationRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.emailConfirmationRepository = emailConfirmationRepository;
    }

    public void sendConfirmationMail(User user) {

        String token = saveEmailConfirmationToken(user);

        emailSenderService.sendMail(token, user.getEmail());
    }

    private String saveEmailConfirmationToken(User user) {

        Map<String, Object> tk = jwtService.generateEmailVerifyToken(user);

        String token = tk.get("emailConfirmationToken").toString();
        Date tokenExpiration = (Date) tk.get("emailConfirmationExpiration");


        var confirmationToken = EmailConfirmation.builder()
                .user(user)
                .confirmationToken(token)
                .tokenExpiration(tokenExpiration)
                .build();
        emailConfirmationRepository.save(confirmationToken);

        return token;
    }

    public void confirmRegisteredEmail(String token) {

        findEmailConfirmationByToken(token)
                .map(emailConfirmation -> {
                    final User user = emailConfirmation.getUser();

                    user.setEnabled(true);

                    userRepository.save(user);

                    deleteEmailConfirmationToken(emailConfirmation.getId());

                    return "redirect:/sign-in";
                }).orElseThrow(
                        () -> new GeneralException("Account Activation", "Email confirmation token expired!")
                );
    }

    public void deleteEmailConfirmationToken(Integer id) {

        emailConfirmationRepository.deleteById(id);
    }


    public Optional<EmailConfirmation> findEmailConfirmationByToken(String token) {

        return emailConfirmationRepository.findEmailConfirmationByConfirmationToken(token);
    }
}
