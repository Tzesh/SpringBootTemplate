package com.tzesh.springtemplate.mapper;

import com.tzesh.springtemplate.base.mapper.BaseMapper;
import com.tzesh.springtemplate.dto.UserDTO;
import com.tzesh.springtemplate.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for {@link User}
 * @see BaseMapper
 * @author tzesh
 */
@Mapper()
public interface UserMapper extends BaseMapper<User, UserDTO> {
}
