CREATE TABLE product (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     description TEXT,
     category VARCHAR(100) NOT NULL,
     image VARCHAR(500),
     price NUMERIC(10, 2) NOT NULL,
     is_active BOOLEAN NOT NULL DEFAULT TRUE
);