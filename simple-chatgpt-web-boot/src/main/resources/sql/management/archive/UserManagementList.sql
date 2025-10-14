DROP TABLE IF EXISTS user_management_list_member;
DROP TABLE IF EXISTS user_management_list;

CREATE TABLE user_management_list (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_list_name VARCHAR(100) NOT NULL,   -- no UNIQUE
    original_file_name VARCHAR(255),
    file_path VARCHAR(512),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_management_list_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    list_id BIGINT NOT NULL,  
    user_name VARCHAR(100) NOT NULL,
    user_key VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(100) NOT NULL,
    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    post_code VARCHAR(20),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_list FOREIGN KEY (list_id) REFERENCES user_management_list(id) ON DELETE CASCADE,
    CONSTRAINT uq_list_member UNIQUE (list_id, email),
    CONSTRAINT uq_list_member_username UNIQUE (list_id, user_name)
);

-- in case we just want to do alter
ALTER TABLE user_management_list_member ADD COLUMN user_key VARCHAR(100) NOT NULL AFTER user_name;
