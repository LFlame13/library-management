package com.example.library_management.mapper;

import com.example.library_management.dto.LibraryBookDTO;
import com.example.library_management.model.LibraryBook;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface LibraryBookMapper {

    @Mapping(source = "bookInfo.id", target = "bookInfoId")
    LibraryBookDTO toDTO(LibraryBook book);
}

