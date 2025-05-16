package com.example.library_management.mapper;

import com.example.library_management.dto.RentalDTO;
import com.example.library_management.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "libraryBook.id", target = "libraryBookId")
    RentalDTO toDTO(Rental rental);
}
