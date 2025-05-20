-- Create books table
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    published_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    description TEXT,
    publisher VARCHAR(255),
    language VARCHAR(50),
    thumbnail_url VARCHAR(1024),
    average_rating DECIMAL(3,2),
    page_count INTEGER
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
    book_id BIGINT NOT NULL,
    user_id INTEGER NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (book_id) REFERENCES books (id),
    FOREIGN KEY (user_id) REFERENCES library_users (id)
);

-- Create book_reviews table
CREATE TABLE IF NOT EXISTS book_reviews (
    id SERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    reviewer_name VARCHAR(255) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    review_date TIMESTAMP NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books (id)
);