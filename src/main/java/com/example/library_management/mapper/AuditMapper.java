package com.example.library_management.mapper;

import com.example.library_management.dto.AuditLogDTO;
import com.example.library_management.model.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "libraryBookId")
    AuditLogDTO toDTO(AuditLog log);
}