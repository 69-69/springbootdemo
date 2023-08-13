package com.assigndevelopers.library_api.user.service;

import com.assigndevelopers.library_api.customException.EntityNotFoundException;
import com.assigndevelopers.library_api.user.entity.User;
import com.assigndevelopers.library_api.user.repository.UserRepository;
import com.assigndevelopers.library_api.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FindUserByEmailOrPhoneOrIpAddress findUserByEmailOrPhoneOrIpAddress;

    public ResponseEntity<Page<User>> getAll(Pageable pageable) {
        Page<User> userPageable = userRepository.findAll(pageable);
        if (!userPageable.hasContent()) {
            throw new EntityNotFoundException(User.class, "Pagination", pageable.next().toString());
        }
        return ResponseEntity.ok(userPageable);
    }

    /*
     * GET User details by ID OR userEmail */
    public ResponseEntity<User> getByIdOrEmail(String userId) {
        return findUserByEmailOrPhoneOrIpAddress.findBy(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () -> new EntityNotFoundException(User.class, Utils.isValidEmail(userId) ? "Email" : "Id ", userId)
                );
    }

    /*
     * DELETE User by ID OR userEmail */
    public ResponseEntity<?> deleteByIdOrEmail(String userId) {

        return findUserByEmailOrPhoneOrIpAddress.findBy(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(
                        () -> new EntityNotFoundException(User.class, Utils.isValidEmail(userId) ? "Email" : "Id ", userId)
                );
    }
}
