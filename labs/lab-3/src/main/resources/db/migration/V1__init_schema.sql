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
