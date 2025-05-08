package com.example.library_management.mapper;

import com.example.library_management.dto.RentalDTO;
import com.example.library_management.model.Rental;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    public RentalDTO toDTO(Rental rental) {
        return new RentalDTO(
                rental.getId(),
                rental.getUser().getId(),
                rental.getLibraryBook().getId(),
                rental.getRentedAt(),
                rental.getDueDate(),
                rental.getReturnedAt()
        );
    }
}
