-- Create books table
CREATE TABLE IF NOT EXISTS books (
    isbn VARCHAR(20) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    published_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Create library_users table
CREATE TABLE IF NOT EXISTS library_users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    membership_number VARCHAR(20) NOT NULL UNIQUE,
    member_since DATE NOT NULL
);

-- Create book_loans table
CREATE TABLE IF NOT EXISTS book_loans (
    id SERIAL PRIMARY KEY,
    book_isbn VARCHAR(20) NOT NULL,
    user_id INTEGER NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (book_isbn) REFERENCES books (isbn),
    FOREIGN KEY (user_id) REFERENCES library_users (id)
);

-- Create book_reviews table
CREATE TABLE IF NOT EXISTS book_reviews (
    id SERIAL PRIMARY KEY,
    book_isbn VARCHAR(20) NOT NULL,
    reviewer_name VARCHAR(255) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    review_date TIMESTAMP NOT NULL,
    FOREIGN KEY (book_isbn) REFERENCES books (isbn)
);