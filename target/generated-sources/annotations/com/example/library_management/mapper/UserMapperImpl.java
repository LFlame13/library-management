package com.example.library_management.mapper;

import com.example.library_management.dto.UserDTO;
import com.example.library_management.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-08T21:52:39+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setPasswordHash( dto.getPassword() );
        user.setId( dto.getId() );
        user.setUsername( dto.getUsername() );

        return user;
    }

    @Override
    public UserDTO toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( user.getId() );
        userDTO.setUsername( user.getUsername() );
        userDTO.setPassword( user.getPassword() );

        userDTO.setRole( user.getUserRoles().isEmpty() ? null : user.getUserRoles().get(0).getRole().getName().name() );

        return userDTO;
    }
}
