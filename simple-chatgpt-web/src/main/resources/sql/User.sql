CREATE DATABASE IF NOT EXISTS chatgpt_db;
USE chatgpt_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Add new columns to users table if not already present
ALTER TABLE users
    ADD COLUMN first_name VARCHAR(100),
    ADD COLUMN last_name VARCHAR(100),
    ADD COLUMN password VARCHAR(255),
    ADD COLUMN address_line_1 VARCHAR(255),
    ADD COLUMN address_line_2 VARCHAR(255),
    ADD COLUMN city VARCHAR(100),
    ADD COLUMN state VARCHAR(100),
    ADD COLUMN post_code VARCHAR(20),
    ADD COLUMN country VARCHAR(100);