package com.example.library_management.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "library_book", schema = "library_db")
@Getter
@Setter
@NoArgsConstructor
public class LibraryBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number", nullable = false, unique = true)
    private Long serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookStatus status;

    @ManyToOne
    @JoinColumn(name = "book_info_id", nullable = false)
    private BookInfo bookInfo;

    public enum BookStatus {
        AVAILABLE,
        RENTED,
        DELETED
    }
}

