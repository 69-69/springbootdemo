package com.assigndevelopers.library_api.user.service;

import com.assigndevelopers.library_api.customException.EntityNotFoundException;
import com.assigndevelopers.library_api.user.Role;
import com.assigndevelopers.library_api.user.entity.User;
import com.assigndevelopers.library_api.user.repository.UserRepository;
import com.assigndevelopers.library_api.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FindUserByEmailOrPhoneOrIpAddress findUserByEmailOrPhoneOrIpAddress;

    @Autowired
    public UserService(UserRepository userRepository, FindUserByEmailOrPhoneOrIpAddress findUserByEmailOrPhoneOrIpAddress) {
        this.userRepository = userRepository;
        this.findUserByEmailOrPhoneOrIpAddress = findUserByEmailOrPhoneOrIpAddress;
    }

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

    // Update Role
    public void updateRole(Integer userId, String role) {

        userRepository.findById(userId)
                .map(user -> {
                    user.setRole(Role.valueOf(role));
                    return userRepository.save(user);
                })
                .orElseThrow(
                        () -> new EntityNotFoundException(User.class, "id", userId.toString())
                );


    }

    /* public void update(Integer id, User updateUser){

        User u = userRepository.findById(id)
                .orElseThrow(
                        ()->new EntityNotFoundException(User.class, "id", id.toString())
                );

        if (!updateUser.getFirstName().isEmpty()
                && !Objects.equals(u.getFirstName(), updateUser.getFirstName())) {
            u.setFirstName(updateUser.getFirstName());
        }
        if (!updateUser.getLastName().isEmpty()
                && !Objects.equals(u.getLastName(), updateUser.getLastName())) {
            u.setLastName(updateUser.getLastName());
        }

        user.setId(u.get().getId());
        user.setRole(Role.USER);
        userRepository.save(user);
    }*/

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
