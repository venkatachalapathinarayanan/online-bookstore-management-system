#!/bin/bash

set -e
set -u

function create_database() {
    local database=$1
    echo "Creating database '$database'"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
        CREATE DATABASE $database;
        GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
    echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
    for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
        create_database $db
        if [ "$db" = "bookstore_users" ]; then
            echo "Creating users table and inserting superadmin and admin in $db"
            psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$db" <<-'EOSQL'
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    user_name VARCHAR(255) NOT NULL UNIQUE,
                    email VARCHAR(255) NOT NULL,
                    full_name VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    phone_number VARCHAR(50) NOT NULL,
                    address VARCHAR(255) NOT NULL,
                    role VARCHAR(50) NOT NULL DEFAULT 'USERS'
                );

            INSERT INTO users (user_name, email, full_name, password, phone_number, address, role)
            SELECT 'superadmin', 'superadmin@bookstore.com', 'Super Admin', '$2a$10$zGWhSUY/qLKRzvGdzKlrsOPasxJWV7tYSU.1JE1n7SvI/SHcXOurS', '+10000000000', 'Kodambakkam-chennai', 'SUPERADMIN'
            WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'superadmin');
            INSERT INTO users (user_name, email, full_name, password, phone_number, address, role)
            SELECT 'admin', 'admin@bookstore.com', 'Admin', '$2a$10$zGWhSUY/qLKRzvGdzKlrsOPasxJWV7tYSU.1JE1n7SvI/SHcXOurS', '+10000000001', 'Nungambakkam-chennai', 'ADMIN'
            WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin');
EOSQL
        fi
        if [ "$db" = "bookstore_inventory" ]; then
            echo "Creating and pre-populating tables in $db"
            psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$db" <<-'EOSQL'
                CREATE TABLE IF NOT EXISTS books (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    genre VARCHAR(100) NOT NULL,
                    isbn VARCHAR(20) NOT NULL UNIQUE,
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL,
                    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
                );
                CREATE TABLE IF NOT EXISTS book_inventory (
                    id SERIAL PRIMARY KEY,
                    book_id INTEGER NOT NULL REFERENCES books(id),
                    quantity INTEGER NOT NULL
                );
                CREATE TABLE IF NOT EXISTS book_price (
                    id SERIAL PRIMARY KEY,
                    book_id INTEGER NOT NULL REFERENCES books(id),
                    price NUMERIC(10,2) NOT NULL
                );

                INSERT INTO books (title, author, genre, isbn, created_at, updated_at, is_deleted) VALUES
                    ('The Pragmatic Programmer', 'Andrew Hunt', 'Programming', '9780201616224', NOW(), NOW(), FALSE),
                    ('Clean Code', 'Robert C. Martin', 'Programming', '9780132350884', NOW(), NOW(), FALSE),
                    ('Effective Java', 'Joshua Bloch', 'Programming', '9780134685991', NOW(), NOW(), FALSE)
                ON CONFLICT (isbn) DO NOTHING;

                INSERT INTO book_inventory (book_id, quantity)
                    SELECT id, 10 FROM books WHERE isbn = '9780201616224' ON CONFLICT DO NOTHING;
                INSERT INTO book_inventory (book_id, quantity)
                    SELECT id, 5 FROM books WHERE isbn = '9780132350884' ON CONFLICT DO NOTHING;
                INSERT INTO book_inventory (book_id, quantity)
                    SELECT id, 7 FROM books WHERE isbn = '9780134685991' ON CONFLICT DO NOTHING;

                INSERT INTO book_price (book_id, price)
                    SELECT id, 45.00 FROM books WHERE isbn = '9780201616224' ON CONFLICT DO NOTHING;
                INSERT INTO book_price (book_id, price)
                    SELECT id, 50.00 FROM books WHERE isbn = '9780132350884' ON CONFLICT DO NOTHING;
                INSERT INTO book_price (book_id, price)
                    SELECT id, 55.00 FROM books WHERE isbn = '9780134685991' ON CONFLICT DO NOTHING;
EOSQL
        fi
    done
    echo "Multiple databases created"
fi

