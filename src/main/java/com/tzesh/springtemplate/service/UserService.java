package com.tzesh.springtemplate.service;

import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.exception.BaseException;
import com.tzesh.springtemplate.base.exception.NotFoundException;
import com.tzesh.springtemplate.base.service.BaseService;
import com.tzesh.springtemplate.dto.UserDTO;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.enums.UserErrorMessage;
import com.tzesh.springtemplate.enums.auth.RoleEnum;
import com.tzesh.springtemplate.mapper.UserMapper;
import com.tzesh.springtemplate.repository.UserRepository;
import com.tzesh.springtemplate.request.user.CreateUserRequest;
import com.tzesh.springtemplate.request.user.UpdateUserRequest;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for {@link User} objects.
 * @author tzesh
 */
@Service
public class UserService extends BaseService<User, UserDTO, UserRepository, UserMapper> {
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for the service
     * @param repository Repository for the service
     */
    @Autowired
    public UserService(UserRepository repository, UserDetailsService service, PasswordEncoder encoder) {
        super(repository, service);
        this.passwordEncoder = encoder;
    }

    /**
     * Initialize mapper for the service
     * @return class of the mapper to be initialized
     */
    @Override
    protected UserMapper initializeMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    /**
     * Update current user
     * @param request Update user request
     *                @see UpdateUserRequest
     * @return UserDTO
     * @throws RuntimeException if email already exists
     */
    public UserDTO updateCurrentUser(UpdateUserRequest request) {
        // get current user
        User user = repository.findByUsername(this.getCurrentUser()).orElseThrow(
                () -> new NotFoundException(
                        GenericErrorMessage.builder().message(
                                UserErrorMessage.USER_NOT_FOUND.getMessage()
                        ).build()
                )
        );

        // get current user id
        Long id = user.getId();

        // update user
        return this.updateUser(id, request);
    }

    /**
     * Get current user from the database
     * @return UserDTO
     */
    public UserDTO getCurrentUserDTO() {
        // get user
        User user = repository.findByUsername(this.getCurrentUser()).orElseThrow(
                () -> new NotFoundException(
                        GenericErrorMessage.builder().message(
                                UserErrorMessage.USER_NOT_FOUND.getMessage()
                        ).build()
                )
        );

        // return user
        return mapper.toDTO(user);
    }

    /**
     * Create a new user in the database
     * @param request request for the new user
     * @return User
     */
    public UserDTO createUser(CreateUserRequest request) {
        // control if email or username is already used
        if (repository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new BaseException(
                    GenericErrorMessage.builder().message(
                            UserErrorMessage.USER_ALREADY_EXISTS.getMessage()
                    ).build()
            );
        }

        // create user
        User user = new User();
        user.setUsername(request.username());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(RoleEnum.valueOf(request.role().name()));
        user.setPassword(passwordEncoder.encode(request.password()));

        // create base auditable fields
        BaseAuditableFields baseAuditableFields = new BaseAuditableFields();

        // set base auditable fields
        baseAuditableFields.setCreatedBy(user.getUsername());
        baseAuditableFields.setCreatedDate(LocalDateTime.now());

        // set base auditable fields to user
        user.setBaseAuditableFields(baseAuditableFields);

        // save user and return
        return mapper.toDTO(this.trySave(user));
    }

    /**
     * Update user in the database
     * @param id id of the user
     * @param request request for the user
     *                @see UpdateUserRequest
     * @return User
     */
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        // check if the entity exists
        this.checkIfEntityExists(id);

        // get user
        User user = repository.findById(id).get();

        // check if email is changed
        if (!user.getEmail().equals(request.email())) {
            // control if email is already used
            if (repository.existsByUsernameOrEmail(null, request.email())) {
                throw new BaseException(
                        GenericErrorMessage.builder().message(
                                UserErrorMessage.USER_ALREADY_EXISTS.getMessage()
                        ).build()
                );
            }
        }

        // update user
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        // update base auditable fields
        BaseAuditableFields baseAuditableFields = user.getBaseAuditableFields();

        // set base auditable fields
        baseAuditableFields.setUpdatedBy(this.getCurrentUser());
        baseAuditableFields.setUpdatedDate(LocalDateTime.now());

        // save user and return
        return mapper.toDTO(this.trySave(user));
    }
}
