ALTER TABLE books
  ADD COLUMN description TEXT,
ADD COLUMN publisher VARCHAR(255),
ADD COLUMN language VARCHAR(50),
ADD COLUMN thumbnail_url VARCHAR(1024),
ADD COLUMN average_rating DECIMAL(3,2),
ADD COLUMN page_count INTEGER;
