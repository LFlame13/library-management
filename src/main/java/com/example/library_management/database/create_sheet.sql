CREATE SCHEMA IF NOT EXISTS library_db;

SET search_path TO library_db;

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
                            id SERIAL PRIMARY KEY,
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            parent_id INT,
                            FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE book_info (
                           id SERIAL PRIMARY KEY,
                           title VARCHAR(255) NOT NULL UNIQUE,
                           author VARCHAR(255) NOT NULL,
                           category_id INT NOT NULL,
                           FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE library_book (
                              id SERIAL PRIMARY KEY,
                              serial_number BIGINT NOT NULL UNIQUE,
                              status VARCHAR(50),
                              book_info_id INT NOT NULL,
                              FOREIGN KEY (book_info_id) REFERENCES book_info(id)
);

CREATE TABLE rentals (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL,
                         library_book_id INT NOT NULL,
                         rented_at TIMESTAMP NOT NULL,
                         due_date TIMESTAMP NOT NULL,
                         returned_at TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id),
                         FOREIGN KEY (library_book_id) REFERENCES library_book(id)
);

CREATE TABLE audit_log (
                           id SERIAL PRIMARY KEY,
                           user_id INT NOT NULL,
                           action VARCHAR(255) NOT NULL,
                           book_id INT NOT NULL,
                           FOREIGN KEY (user_id) REFERENCES users(id),
                           FOREIGN KEY (book_id) REFERENCES library_book(id)
);
