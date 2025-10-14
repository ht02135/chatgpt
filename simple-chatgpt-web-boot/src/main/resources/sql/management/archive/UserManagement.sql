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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE user_management
    ADD COLUMN active BOOLEAN DEFAULT TRUE AFTER country,
    ADD COLUMN locked BOOLEAN DEFAULT FALSE AFTER active,
    ADD COLUMN last_login_ip VARCHAR(45) NULL AFTER locked,
    ADD COLUMN last_login_at TIMESTAMP NULL AFTER last_login_ip,
    ADD COLUMN jwt_secret_version VARCHAR(50) NULL AFTER last_login_at;
    
CREATE TABLE role_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE role_group_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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

CREATE TABLE page_role_group_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    url_pattern VARCHAR(255) NOT NULL,
    role_group_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_page_role_group FOREIGN KEY (role_group_id) REFERENCES role_group_management(id)
);

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



