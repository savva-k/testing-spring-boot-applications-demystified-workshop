-- Sample data for tests
INSERT INTO books (id, isbn, title, author, published_date, status)
VALUES 
    (1, '978-0-13-235088-4', 'Clean Code', 'Robert C. Martin', '2008-08-01', 'AVAILABLE'),
    (2, '978-0-13-468599-1', 'Effective Java', 'Joshua Bloch', '2017-10-24', 'AVAILABLE'),
    (3, '978-0-20-161622-4', 'The Pragmatic Programmer', 'Andrew Hunt', '1999-10-20', 'BORROWED');