CREATE TABLE IF NOT EXISTS id_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name_user VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    reset_token VARCHAR(255),
    reset_token_expires_at DATETIME
); 