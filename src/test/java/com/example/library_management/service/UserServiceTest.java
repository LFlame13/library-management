package com.example.library_management.service;

import com.example.library_management.dao.RentalDAO;
import com.example.library_management.dao.RoleDAO;
import com.example.library_management.dao.UserDAO;
import com.example.library_management.dao.UserRoleDAO;
import com.example.library_management.dto.UpdateUserDTO;
import com.example.library_management.model.*;
import com.example.library_management.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private RoleDAO roleDAO;

    @Mock
    private UserRoleDAO userRoleDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RentalDAO rentalDAO;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("password");

        role = new Role();
        role.setId(1L);
        role.setName(RoleType.ROLE_USER);
    }

    @Test
    void register_ValidUser_ReturnsToken() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(roleDAO.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(role));
        when(jwtUtil.generateToken(eq("testuser"), anyList())).thenReturn("token123");

        String token = userService.register(user, "user");

        verify(userDAO).save(user);
        verify(userRoleDAO).save(any(UserRole.class));
        assertEquals("token123", token);
    }

    @Test
    void register_InvalidRole_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(user, "NONAME");
        });
    }

    @Test
    void updatePartUser_UpdateUsernameAndPassword_Success() {
        UpdateUserDTO dto = new UpdateUserDTO("newUser", "newPass", null);
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(userDAO.findByUsername("newUser")).thenReturn(null);
        when(passwordEncoder.encode("newPass")).thenReturn("hashedPass");

        userService.updatePartUser(1L, dto);

        verify(userDAO).update(user);
        assertEquals("newUser", user.getUsername());
        assertEquals("hashedPass", user.getPasswordHash());
    }

    @Test
    void updatePartUser_InvalidRole_ThrowsException() {
        UpdateUserDTO dto = new UpdateUserDTO(null, null, "ROLE");
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePartUser(1L, dto);
        });
    }

    @Test
    void updatePartUser_UsernameAlreadyExists_ThrowsException() {
        UpdateUserDTO dto = new UpdateUserDTO("existingUser", null, null);
        User existingUser = new User();
        existingUser.setId(2L);
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        when(userDAO.findByUsername("existingUser")).thenReturn(existingUser);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePartUser(1L, dto);
        });
    }

    @Test
    void updatePartUser_AllFieldsNull_ThrowsException() {
        UpdateUserDTO dto = new UpdateUserDTO(null, null, null);
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePartUser(1L, dto);
        });
    }

    @Test
    void deleteById_UserExists_DeletesSuccessfully() {
        Long userId = 1L;
        when(userDAO.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);

        verify(userDAO).delete(user);
    }

    @Test
    void deleteById_UserDoesNotExist_ThrowsEntityNotFoundException() {
        Long userId = 1L;
        when(userDAO.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(userId));
    }


    @Test
    void loadUserByUsername_UserFound_ReturnsUser() {
        when(userDAO.findByUsername("testuser")).thenReturn(user);

        User result = (User) userService.loadUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void loadUserByUsername_NotFound_ThrowsException() {
        when(userDAO.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }

    @Test
    void getUserById_UserFound_ReturnsUser() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(1L);

        assertEquals(1L, found.getId());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }

    @Test
    void findByUsername_UserExists_ReturnsUser() {
        when(userDAO.findByUsername("testuser")).thenReturn(user);

        User found = userService.findByUsername("testuser");

        assertEquals("testuser", found.getUsername());
    }

    @Test
    void findByUsername_NotFound_ThrowsException() {
        when(userDAO.findByUsername("Viktor")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findByUsername("Viktor");
        });
    }
}
