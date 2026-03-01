package com.tzesh.springtemplate.mapper;

import com.tzesh.springtemplate.base.mapper.AuditableMapper;
import com.tzesh.springtemplate.base.mapper.BaseMapper;
import com.tzesh.springtemplate.dto.UserDTO;
import com.tzesh.springtemplate.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for {@link User}
 * @see BaseMapper
 * @author tzesh
 */
@Mapper(uses = {AuditableMapper.class})
public interface UserMapper extends BaseMapper<User, UserDTO> {
    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tokenEntities", ignore = true)
    User toEntity(UserDTO dto);
}