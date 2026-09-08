CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,                           -- UUID String
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE
);

-- Tạo index để tối ưu truy vấn
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);