package com.tzesh.springtemplate.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.tzesh.springtemplate.dto.UserDTO;
import com.tzesh.springtemplate.entity.User;
import com.tzesh.springtemplate.mapper.UserMapper;
import com.tzesh.springtemplate.repository.user.UserRepository;

class UserServiceTest {
    private static final UUID USER_ID_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID USER_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userService, "mapper", userMapper);
    }

    @Test
    void testFindByIdReturnsUserDTO() {
        User user = new User();
        user.setId(USER_ID_1);
        UserDTO userDTO = new UserDTO();
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        UserDTO result = userService.findById(USER_ID_1);
        assertNotNull(result);
    }

    @Test
    void testFindByIdThrowsNotFoundIfNotFound() {
        when(userRepository.findById(USER_ID_2)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findById(USER_ID_2));
    }

    @Test
    void testSaveUser() {
        org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);
        User user = new User();
        UserDTO userDTO = new UserDTO();
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userDTO);
        UserDTO result = userService.save(userDTO);
        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setId(USER_ID_1);
        when(userRepository.findById(USER_ID_1)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(USER_ID_1);
        assertDoesNotThrow(() -> userService.deleteById(USER_ID_1));
        verify(userRepository, times(1)).deleteById(USER_ID_1);
        // Do NOT verify userRepository.delete(user) as your service does not call it
    }

    @Test
    void testFindAllUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(anyList())).thenAnswer(invocation -> {
             List<User> argument = invocation.getArgument(0);
             return argument.stream().map(u -> new UserDTO()).collect(java.util.stream.Collectors.toList());
        });
        List<UserDTO> result = userService.findAll();
        assertEquals(2, result.size());
    }
}
