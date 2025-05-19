-- Create books table
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    published_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Create book_reviews table
CREATE TABLE IF NOT EXISTS book_reviews (
    id BIGSERIAL PRIMARY KEY,
    book_isbn VARCHAR(20) NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_date TIMESTAMP NOT NULL
);

-- Create book_loans table
CREATE TABLE IF NOT EXISTS book_loans (
    id BIGSERIAL PRIMARY KEY,
    book_isbn VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE
);