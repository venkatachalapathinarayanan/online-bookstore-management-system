-- Insert sample books
INSERT INTO books (title, author, genre, isbn, created_at, updated_at, is_deleted) VALUES
    ('The Pragmatic Programmer', 'Andrew Hunt', 'Programming', '9780201616224', NOW(), NOW(), FALSE),
    ('Clean Code', 'Robert C. Martin', 'Programming', '9780132350884', NOW(), NOW(), FALSE),
    ('Effective Java', 'Joshua Bloch', 'Programming', '9780134685991', NOW(), NOW(), FALSE)
ON CONFLICT (isbn) DO NOTHING;

-- Insert inventory for sample books (using UPSERT pattern)
INSERT INTO books_inventory (book_id, quantity)
    SELECT id, 10 FROM books WHERE isbn = '9780201616224'
ON CONFLICT (book_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO books_inventory (book_id, quantity)
    SELECT id, 5 FROM books WHERE isbn = '9780132350884'
ON CONFLICT (book_id) DO UPDATE SET quantity = EXCLUDED.quantity;

INSERT INTO books_inventory (book_id, quantity)
    SELECT id, 7 FROM books WHERE isbn = '9780134685991'
ON CONFLICT (book_id) DO UPDATE SET quantity = EXCLUDED.quantity;

-- Insert prices for sample books (using UPSERT pattern)
INSERT INTO books_price (book_id, price)
    SELECT id, 45.00 FROM books WHERE isbn = '9780201616224'
ON CONFLICT (book_id) DO UPDATE SET price = EXCLUDED.price;

INSERT INTO books_price (book_id, price)
    SELECT id, 50.00 FROM books WHERE isbn = '9780132350884'
ON CONFLICT (book_id) DO UPDATE SET price = EXCLUDED.price;

INSERT INTO books_price (book_id, price)
    SELECT id, 55.00 FROM books WHERE isbn = '9780134685991'
ON CONFLICT (book_id) DO UPDATE SET price = EXCLUDED.price; 