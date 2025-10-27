CREATE TABLE property_management (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    property_name VARCHAR(100) UNIQUE,
    property_key VARCHAR(100) UNIQUE,
    type VARCHAR(255),
    value VARCHAR(255)
);
