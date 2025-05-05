CREATE TABLE IF NOT EXISTS reset_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    attempt_time DATETIME NOT NULL,
    INDEX idx_email_time (email, attempt_time)
); 