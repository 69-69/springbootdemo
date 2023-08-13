package com.assigndevelopers.library_api.user;

import com.assigndevelopers.library_api.user.entity.User;
import com.assigndevelopers.library_api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
// For the Purpose of SpringDoc OpenAPI Swagger-UI
@Tag(name = "Users Endpoint")
@Tags
public class UserController {

    private final UserService userService;

    // For the Purpose of SpringDoc OpenAPI Swagger-UI
    @Operation(
            description = "Signup new users",
            summary = "For all Signup new users",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )

    @GetMapping
    public ResponseEntity<Page<User>> getAll(
            @ParameterObject /*For the Purpose of SpringDoc OpenAPI Swagger-UI*/
            Pageable pageable) {

        return userService.getAll(pageable);
    }

    /*
     * GET User details by userId OR userEmail*/
    @GetMapping("/{userId}")
    public ResponseEntity<User> getByIdOrEmail(@PathVariable String userId) {

        return userService.getByIdOrEmail(userId);
    }

    /*
     * DELETE User by ID OR userEmail*/
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable String userId) {

        return userService.deleteByIdOrEmail(userId);
    }
}
