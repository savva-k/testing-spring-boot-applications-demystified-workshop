-- Insert sample books
INSERT INTO books (isbn, title, author, published_date, status)
VALUES
    ('978-0-13-235088-4', 'Clean Code', 'Robert C. Martin', '2008-08-01', 'AVAILABLE'),
    ('978-0-13-468599-1', 'Effective Java', 'Joshua Bloch', '2017-10-24', 'AVAILABLE'),
    ('978-0-20-161622-4', 'The Pragmatic Programmer', 'Andrew Hunt', '1999-10-20', 'BORROWED'),
    ('978-0-32-112742-6', 'Domain-Driven Design', 'Eric Evans', '2003-08-30', 'RESERVED'),
    ('978-0-13-449416-6', 'Clean Architecture', 'Robert C. Martin', '2017-09-10', 'AVAILABLE');

-- Insert sample book reviews
INSERT INTO book_reviews (book_isbn, rating, comment, created_date)
VALUES
    ('978-0-13-235088-4', 5, 'Excellent book for clean coding practices!', '2023-01-10 14:30:00'),
    ('978-0-13-235088-4', 4, 'Very helpful, but some examples are outdated.', '2023-02-15 09:45:00'),
    ('978-0-13-468599-1', 5, 'The best book on Java programming!', '2023-03-20 16:15:00'),
    ('978-0-20-161622-4', 3, 'Good concepts but a bit dated now.', '2023-02-25 11:20:00'),
    ('978-0-32-112742-6', 4, 'Great introduction to DDD concepts.', '2023-03-05 13:40:00'),
    ('978-0-13-449416-6', 5, 'A must-read for software architects!', '2023-03-15 10:30:00');

-- Insert sample book loans
INSERT INTO book_loans (book_isbn, user_id, loan_date, due_date, return_date)
VALUES
    ('978-0-13-235088-4', 1, '2023-04-01', '2023-04-15', '2023-04-12'),
    ('978-0-13-468599-1', 2, '2023-04-05', '2023-04-19', NULL),
    ('978-0-20-161622-4', 3, '2023-04-10', '2023-04-24', NULL),
    ('978-0-32-112742-6', 4, '2023-03-15', '2023-03-29', '2023-03-25'),
    ('978-0-13-449416-6', 5, '2023-03-20', '2023-04-03', '2023-04-05');