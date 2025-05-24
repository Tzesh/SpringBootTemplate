package com.tzesh.springtemplate.service;

import com.tzesh.springtemplate.base.entity.field.BaseAuditableFields;
import com.tzesh.springtemplate.base.error.GenericErrorMessage;
import com.tzesh.springtemplate.base.exception.BaseException;
import com.tzesh.springtemplate.base.exception.NotFoundException;
import com.tzesh.springtemplate.base.service.BaseService;
import com.tzesh.springtemplate.dto.UserDTO;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.enumeration.UserErrorMessage;
import com.tzesh.springtemplate.enumeration.auth.Role;
import com.tzesh.springtemplate.mapper.UserMapper;
import com.tzesh.springtemplate.repository.user.UserRepository;
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
    public UserService(final UserRepository repository, final UserDetailsService service, final PasswordEncoder encoder) {
        super(repository, service);
        this.passwordEncoder = encoder;
    }

    @Override
    protected UserMapper initializeMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    public UserDTO updateCurrentUser(UpdateUserRequest request) {
        final User user = repository.findByUsername(this.getCurrentUser()).orElseThrow(
                () -> new NotFoundException(
                        GenericErrorMessage.builder().message(
                                UserErrorMessage.USER_NOT_FOUND.getMessage()
                        ).build()
                )
        );
        final Long id = user.getId();
        return this.updateUser(id, request);
    }

    public UserDTO getCurrentUserDTO() {
        final User user = repository.findByUsername(this.getCurrentUser()).orElseThrow(
                () -> new NotFoundException(
                        GenericErrorMessage.builder().message(
                                UserErrorMessage.USER_NOT_FOUND.getMessage()
                        ).build()
                )
        );
        return mapper.toDTO(user);
    }

    public UserDTO createUser(final CreateUserRequest request) {
        if (repository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new BaseException(
                    GenericErrorMessage.builder().message(
                            UserErrorMessage.USER_ALREADY_EXISTS.getMessage()
                    ).build()
            );
        }
        final User user = new User();
        user.setUsername(request.username());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(Role.valueOf(request.role().name()));
        user.setPassword(passwordEncoder.encode(request.password()));
        final BaseAuditableFields baseAuditableFields = new BaseAuditableFields();
        baseAuditableFields.setCreatedBy(user.getUsername());
        baseAuditableFields.setCreatedDate(LocalDateTime.now());
        user.setAuditableFields(baseAuditableFields);
        return mapper.toDTO(this.trySave(user));
    }

    public UserDTO updateUser(final Long id, final UpdateUserRequest request) {
        final User user = repository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        GenericErrorMessage.builder().message(
                                UserErrorMessage.USER_NOT_FOUND.getMessage()
                        ).build()
                )
        );

        if (!user.getEmail().equals(request.email())) {
            if (repository.existsByUsernameOrEmail(null, request.email())) {
                throw new BaseException(
                        GenericErrorMessage.builder().message(
                                UserErrorMessage.USER_ALREADY_EXISTS.getMessage()
                        ).build()
                );
            }
        }

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        final BaseAuditableFields baseAuditableFields = user.getAuditableFields();
        baseAuditableFields.setUpdatedBy(this.getCurrentUser());
        baseAuditableFields.setUpdatedDate(LocalDateTime.now());

        return mapper.toDTO(this.trySave(user));
    }
}
