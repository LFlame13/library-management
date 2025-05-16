INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO users (username, password_hash) VALUES
                                                           ('Виктор', '$2a$10$uMXyt2DE3P6OJ5Ayx/WhB.G0gfJePGyGkm1ElAEtIT0n8U73qM13y'),
                                                           ('Влад', '$2a$10$FQn2on6KZBxuGBYHTDZoxOZ1mMxjQz3xKixRzNiGJtpbgDPREKnq6'),
                                                           ('Илья', '$2a$10$g.6owqsM9rjJK/G4RfJpI.nZDbYbZrnxAi0bR2z1SVCZgZnQoD9Ui'),
                                                            ('Павел', '$2b$10$mUOn5OD.sqIINWrKyrgB5.lI/8qVsAVeYCdtPyS/7B.rWVB3fjYEK'),
                                                            ('Алексей', '$2b$10$DmYVNPp7cfMUgVMP8wPF4.lUBEXDfs.gsFNbIYp6CzSib5ckcB5i6');

INSERT INTO user_roles (user_id, role_id) VALUES
                                                         (1,1),
                                                         (2,1),
                                                         (3,1),
                                                         (4,1),
                                                         (5,1);

INSERT INTO categories (name, parent_id) VALUES
                                                        ('Наука', null),
                                                        ('Химия', null),
                                                        ('Фантастика', null),
                                                        ('Космос', 3 ),
                                                        ('Боевик', null),
                                                        ('Военный', null);

INSERT INTO book_info (title, author, category_id) VALUES
                                                                  ('История времени', 'Иван Дынин', 1),
                                                                  ('Органическая химия', 'Паулс Зволиньш', 2),
                                                                  ('Машина времени', 'Иван Васильевич', 3),
                                                                  ('Космический пират', 'Артур Камышев', 4),
                                                                  ('Один выстрел', 'Артур Камышев', 5),
                                                                  ('Честь имею', 'Александр Бушков', 6);



INSERT INTO library_book (serial_number, status, book_info_id) VALUES
                                                                              (123456, 'AVAILABLE', 1),
                                                                              (122345, 'AVAILABLE', 2),
                                                                              (133456, 'AVAILABLE', 3),
                                                                              (144567, 'RENTED', 4),
                                                                              (12222456, 'AVAILABLE', 5),
                                                                              (1466664567, 'AVAILABLE', 6);


INSERT INTO rentals (user_id, library_book_id, rented_at, due_date, returned_at)
VALUES
    ( 1, 1, '2025-04-01 10:00:00', '2025-04-08 10:00:00', '2025-04-05 10:00:00'),
    ( 2, 2, '2025-03-20 14:30:00', '2025-03-27 14:30:00', '2025-03-23 14:30:00'),
    ( 3, 3, '2025-04-10 09:15:00', '2025-04-17 09:15:00', '2025-04-15 09:15:00'),
    ( 3, 4, '2025-04-10 09:15:00', '2025-04-17 09:15:00', NULL);

INSERT INTO audit_log (user_id, action, book_id) VALUES
                                                                (1,'BOOK_RENTED',1),
                                                                (2,'BOOK_RENTED',2),
                                                                (3,'BOOK_RENTED',3),
                                                                (3,'BOOK_RENTED',4),
                                                                (1,'BOOK_RETURNED',1),
                                                                (2,'BOOK_RETURNED',2),
                                                                (3,'BOOK_RETURNED',3);


