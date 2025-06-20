-- Create users table only if connected to bookstore_users database
DO $$
BEGIN
    IF current_database() = 'bookstore_users' THEN
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

        -- Insert default superadmin user if not exists
        INSERT INTO users (user_name, email, full_name, password, phone_number, address, role)
        SELECT 'superadmin', 'superadmin@bookstore.com', 'Super Admin', '$2a$10$7QJ8QwQwQwQwQwQwQwQwQeQwQwQwQwQwQwQwQwQwQwQwQwQwQwQw', '+10000000000', 'Admin Address', 'SUPERADMIN'
        WHERE NOT EXISTS (
            SELECT 1 FROM users WHERE user_name = 'superadmin'
        );
    END IF;
END $$;
