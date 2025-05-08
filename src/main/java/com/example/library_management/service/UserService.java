package com.example.library_management.service;

import com.example.library_management.dao.RentalDAO;
import com.example.library_management.dao.RoleDAO;
import com.example.library_management.dao.UserDAO;
import com.example.library_management.dao.UserRoleDAO;
import com.example.library_management.dto.UpdateUserDTO;
import com.example.library_management.model.Rental;
import com.example.library_management.model.Role;
import com.example.library_management.model.RoleType;
import com.example.library_management.model.User;
import com.example.library_management.model.UserRole;
import com.example.library_management.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final UserRoleDAO userRoleDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RentalDAO rentalDAO;

    public UserService(UserDAO userDAO, RoleDAO roleDAO, UserRoleDAO userRoleDAO, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RentalDAO rentalDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.userRoleDAO = userRoleDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.rentalDAO = rentalDAO;

    }

    @Transactional
    public String register(User user, String roleName) {
        roleName = "ROLE_" + roleName.trim().toUpperCase();
        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Имя пользователя уже занято");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPasswordHash(encodedPassword);

        RoleType roleType;
        try {
            roleType = RoleType.valueOf(roleName);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Недопустимая роль: " + roleName +
                    ". Доступные роли: USER, ADMIN");
        }

        userDAO.save(user);

        Role role = roleDAO.findByName(roleType)
                .orElseThrow(() -> new EntityNotFoundException("Роль не найдена в базе: " + roleType));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleDAO.save(userRole);

        log.info("Пользователь '{}' зарегистрирован с ролью '{}'", user.getUsername(), roleType);

        List<String> roles = List.of(roleType.name().replace("ROLE_", ""));
        return jwtUtil.generateToken(user.getUsername(), roles);
    }

    // Обновление данных
    @Transactional
    public void updatePartUser(Long id, UpdateUserDTO dto) {
        User user = userDAO.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с ID " + id + " не найден"));

        if (dto.getUsername() == null && dto.getPassword() == null && dto.getRole() == null) {
            throw new IllegalArgumentException("Не указано ни одного поля для обновления");
        }

        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            User existingUser = userDAO.findByUsername(dto.getUsername());
            if (existingUser != null && !existingUser.getId().equals(id)) {
                throw new IllegalArgumentException("Имя пользователя '" + dto.getUsername() + "' уже занято");
            }
            user.setUsername(dto.getUsername().trim());
        }

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(dto.getPassword().trim());
            user.setPasswordHash(encodedPassword);
        }

        if (dto.getRole() != null && !dto.getRole().trim().isEmpty()) {
            String roleInput = dto.getRole().trim().toUpperCase();
            String roleString = "ROLE_" + roleInput;

            try {
                RoleType roleType = RoleType.valueOf(roleString);
                Role roleEntity = roleDAO.findByName(roleType)
                        .orElseThrow(() -> new EntityNotFoundException("Роль не найдена: " + roleString));


                userRoleDAO.findByUserId(user.getId()).forEach(userRoleDAO::delete);

                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(roleEntity);
                userRoleDAO.save(userRole);

            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Недопустимая роль: " + roleInput +
                        ". Доступные роли: USER, ADMIN");
            }
        }
        userDAO.update(user);
    }
    // Удаление пользователя
    @Transactional
    public void deleteById(Long id) {
        User user = userDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + id + " не найден"));

        List<Rental> activeRentals = rentalDAO.findActiveByUserId(id);
        if (!activeRentals.isEmpty()) {
            String books = activeRentals.stream()
                    .map(r -> "Книга[id=" + r.getLibraryBook().getId() + "]")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("неизвестно");

            throw new IllegalStateException("Нельзя удалить пользователя. Он не вернул книги: " + books);
        }

        userRoleDAO.deleteByUserId(id);
        userDAO.delete(user);
        log.info("Пользователь '{}' успешно удален", user.getUsername());
    }


    // loadUserByUsername UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Попытка загрузить пользователя с именем '{}'", username);
        User user = userDAO.findByUsername(username);
        if (user == null) {
            log.error("Пользователь с именем '{}' не найден", username);
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }
        log.info("Пользователь с именем '{}' найден", username);
        return user;
    }

    // Пользователь по id
    @Transactional
    public User getUserById(Long id) {
        log.info("Попытка получить пользователя с ID '{}'", id);

        try {
            User user = userDAO.findById(id).orElse(null);

            if (user == null) {
                log.error("Пользователь с ID '{}' не найден", id);
                throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
            }

            log.info("Пользователь с ID '{}' найден", id);
            return user;
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя с ID '{}': {}", id, e.getMessage());
            throw e;
        }
    }

    // По имени
    public User findByUsername(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return user;
    }
}