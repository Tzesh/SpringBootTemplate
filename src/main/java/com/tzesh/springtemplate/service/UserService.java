package com.tzesh.springtemplate.service;

import com.tzesh.springtemplate.dto.UserDTO;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.mapper.UserMapper;
import com.tzesh.springtemplate.repository.UserRepository;
import com.tzesh.springtemplate.request.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

/**
 * Service for {@link User} objects.
 * @author tzesh
 */
@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository repository;
    final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    /**
     * Update user
     * @param request Update request
     * @return UserDTO
     */
    public UserDTO update(RegisterRequest request) {
        // TODO: implement update user
        throw new RuntimeException("Not implemented");
    }
}
