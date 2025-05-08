package com.example.library_management.mapper;

import com.example.library_management.dto.UserDTO;
import com.example.library_management.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "password", target = "passwordHash")
    @Mapping(target = "userRoles", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "role", expression = "java(user.getUserRoles().isEmpty() ? null : user.getUserRoles().get(0).getRole().getName().name())")
    UserDTO toDto(User user);
}
