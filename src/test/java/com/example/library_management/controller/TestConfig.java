package com.example.library_management.controller;

import com.example.library_management.mapper.AuditMapper;
import com.example.library_management.mapper.UserMapper;
import com.example.library_management.mapper.RentalMapper;
import com.example.library_management.service.AuditService;
import com.example.library_management.service.CategoryService;
import com.example.library_management.service.LibraryBookService;
import com.example.library_management.service.UserService;
import com.example.library_management.service.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mockito.Mockito;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {

    @Bean
    public UserMapper userMapper() {
        return Mockito.mock(UserMapper.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public RentalService rentalService() {
        return Mockito.mock(RentalService.class);
    }

    @Bean
    public RentalMapper rentalMapper() {
        return Mockito.mock(RentalMapper.class);
    }

    @Bean
    public LibraryBookService libraryBookService() {
        return Mockito.mock(LibraryBookService.class);
    }

    @Primary
    @Bean
    public CategoryService categoryService() {
        return Mockito.mock(CategoryService.class);
    }

    @Bean
    public AuditService auditService() {
        return Mockito.mock(AuditService.class);
    }

    @Bean
    public AuditMapper auditMapper() {
        return Mockito.mock(AuditMapper.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
