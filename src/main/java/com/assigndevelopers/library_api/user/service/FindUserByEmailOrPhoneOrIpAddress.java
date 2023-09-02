package com.assigndevelopers.library_api.user.service;

import com.assigndevelopers.library_api.user.entity.User;
import com.assigndevelopers.library_api.user.repository.UserRepository;
import com.assigndevelopers.library_api.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindUserByEmailOrPhoneOrIpAddress {
    private final UserRepository userRepository;

    @Autowired
    public FindUserByEmailOrPhoneOrIpAddress(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Find Client/User by IP or Email or Phone number based on Input
    public Optional<User> findBy(String userInput) {
        return (Utils.isValidEmail(userInput) ?
                userRepository.findByEmail(userInput)
                : (Utils.isValidPhone(userInput) ?
                userRepository.findByPhone(userInput)
                : userRepository.findByIpAddress(userInput))
        );
    }
}
