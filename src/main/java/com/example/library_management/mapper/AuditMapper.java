package com.example.library_management.mapper;

import com.example.library_management.dto.AuditLogDTO;
import com.example.library_management.model.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditMapper {
    public AuditLogDTO toDTO(AuditLog log) {
        return new AuditLogDTO(
                log.getId(),
                log.getUser().getId(),
                log.getAction(),
                log.getBook().getId()
        );
    }
}
