-- Insert default superadmin user
INSERT INTO users (user_name, email, full_name, password, phone_number, address, role)
SELECT 'superadmin', 'superadmin@bookstore.com', 'Super Admin', 
       '$2a$10$zGWhSUY/qLKRzvGdzKlrsOPasxJWV7tYSU.1JE1n7SvI/SHcXOurS', 
       '+10000000000', 'Kodambakkam-chennai', 'SUPERADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'superadmin');

-- Insert default admin user
INSERT INTO users (user_name, email, full_name, password, phone_number, address, role)
SELECT 'admin', 'admin@bookstore.com', 'Admin', 
       '$2a$10$zGWhSUY/qLKRzvGdzKlrsOPasxJWV7tYSU.1JE1n7SvI/SHcXOurS', 
       '+10000000001', 'Nungambakkam-chennai', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin'); 