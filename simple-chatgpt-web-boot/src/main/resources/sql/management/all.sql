-- Drop tables if they exist
DROP TABLE IF EXISTS user_management_list_member;
DROP TABLE IF EXISTS user_management_list;
DROP TABLE IF EXISTS user_management_role_group_mapping;
DROP TABLE IF EXISTS page_role_group_management;
DROP TABLE IF EXISTS role_group_role_mapping;
DROP TABLE IF EXISTS role_group_management;
DROP TABLE IF EXISTS role_management;
DROP TABLE IF EXISTS user_management;
DROP TABLE IF EXISTS property_management;

-- Create property management table
CREATE TABLE property_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    property_name VARCHAR(100) UNIQUE,
    property_key VARCHAR(100) UNIQUE,
    type VARCHAR(255),
    value VARCHAR(255)
);

-- Create user management table
CREATE TABLE user_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(100) UNIQUE,
    user_key VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    post_code VARCHAR(20),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    locked BOOLEAN DEFAULT FALSE,
    last_login_ip VARCHAR(45) NULL,
    last_login_at TIMESTAMP NULL,
    jwt_secret_version VARCHAR(50) NULL
);

-- Create role management table
CREATE TABLE role_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create role group management table
CREATE TABLE role_group_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create role group role mapping table
CREATE TABLE role_group_role_mapping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_group_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_group FOREIGN KEY (role_group_id) REFERENCES role_group_management(id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role_management(id),
    UNIQUE (role_group_id, role_id)
);

-- Create page role group management table
CREATE TABLE page_role_group_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    url_pattern VARCHAR(255) NOT NULL,
    role_group_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_page_role_group FOREIGN KEY (role_group_id) REFERENCES role_group_management(id)
);

-- Create user management role group mapping table
CREATE TABLE user_management_role_group_mapping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_group_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user_management(id),
    CONSTRAINT fk_user_role_group FOREIGN KEY (role_group_id) REFERENCES role_group_management(id),
    UNIQUE (user_id, role_group_id)
);

-- Create user management list table
CREATE TABLE user_management_list (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_list_name VARCHAR(100) NOT NULL,   -- no UNIQUE
    original_file_name VARCHAR(255),
    file_path VARCHAR(512),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create user management list member table
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

-- ///////////////////////////////////////
-- additional change 20251016

CREATE TABLE page_management (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url_pattern VARCHAR(255) NOT NULL,
    delimitRoleGroups VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add delimitRoles to role_group_management
ALTER TABLE role_group_management
ADD COLUMN delimitRoles VARCHAR(255);

-- Add delimitRoleGroups to user_management
ALTER TABLE user_management
ADD COLUMN delimitRoleGroups VARCHAR(255);



