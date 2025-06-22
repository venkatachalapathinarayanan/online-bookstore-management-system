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
                    id BIGSERIAL PRIMARY KEY,
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
        # Note: Removed data insertion for bookstore_inventory - Flyway migrations will handle this
    done
    echo "Multiple databases created"
fi

