package com.tzesh.springtemplate.controller;

import com.tzesh.springtemplate.base.response.BaseResponse;
import com.tzesh.springtemplate.dto.UserDTO;
import com.tzesh.springtemplate.request.user.CreateUserRequest;
import com.tzesh.springtemplate.request.user.UpdateUserRequest;
import com.tzesh.springtemplate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller class for handling user requests
 * @see UserService
 * @author tzesh
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "2. User Controller", description = "User operations")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    /**
     * Create a new user
     * @param request the request body
     * @return the response entity
     */
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user (ADMIN)", description = "Create a new user with the given details and return the user")
    public ResponseEntity<BaseResponse<UserDTO>> createUser(@RequestBody @Valid CreateUserRequest request) {
        // call the create method in the user service
        UserDTO userDTO = userService.createUser(request);

        // return the response
        return BaseResponse.create(userDTO, HttpStatus.CREATED).message("User created successfully").build();
    }

    /**
     * Get all users
     * @return the response entity
     */
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (ADMIN)", description = "Get all users and return the users")
    public ResponseEntity<BaseResponse<List<UserDTO>>> getAllUsers() {
        // call the get all method in the user service
        List<UserDTO> userDTOList = userService.findAll();

        // return the response
        return BaseResponse.ok(userDTOList).message("Users retrieved successfully").build();
    }

    /**
     * Get user by id
     * @param id the user id
     * @return the response entity
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get a user (ADMIN)", description = "Get a user with the given id and return the user")
    public ResponseEntity<BaseResponse<UserDTO>> getUser(@PathVariable @NotNull Long id) {
        // call the get method in the user service
        UserDTO userDTO = userService.findById(id);

        // return the response
        return BaseResponse.ok(userDTO).message("User retrieved successfully").build();
    }

    /**
     * Delete user by id
     * @param id the user id
     * @return the response entity
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a user (ADMIN)", description = "Delete a user with the given id and return the user")
    public ResponseEntity<BaseResponse<UserDTO>> deleteUser(@PathVariable @NotNull Long id) {
        // call the delete method in the user service
        UserDTO userDTO = userService.deleteById(id);

        // return the response
        return BaseResponse.ok(userDTO).message("User deleted successfully").build();
    }

    /**
     * Update a user
     * @param id the user id
     * @param request the request body
     * @return the response entity
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a user (ADMIN)", description = "Update a user with the given details and return the user")
    public ResponseEntity<BaseResponse<UserDTO>> updateUser(@PathVariable @NotNull Long id, @RequestBody @Valid UpdateUserRequest request) {
        // call the update method in the user service
        UserDTO userDTO = userService.updateUser(id, request);

        // return the response
        return BaseResponse.ok(userDTO).message("User updated successfully").build();
    }

    /**
     * Get the current user
     * @return the response entity
     */
    @GetMapping("/current")
    @Operation(summary = "Get the current user", description = "Get the current user and return the user")
    public ResponseEntity<BaseResponse<UserDTO>> getCurrentUser() {
        // call the get current method in the user service
        UserDTO userDTO = userService.getCurrentUserDTO();

        // return the response with the current user
        return BaseResponse.ok(userDTO).message("User retrieved successfully").build();
    }

    /**
     * Update the current user
     * @param request the request body
     * @return the response entity
     */
    @PatchMapping("/current")
    @Operation(summary = "Update the current user", description = "Update the current user with the given details and return the user")
    public ResponseEntity<BaseResponse<UserDTO>> updateCurrentUser(@RequestBody @Valid UpdateUserRequest request) {
        // call the update method in the user service
        UserDTO userDTO = userService.updateCurrentUser(request);

        // return the response with the current user
        return BaseResponse.ok(userDTO).message("User updated successfully").build();
    }
}
