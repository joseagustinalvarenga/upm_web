CREATE TABLE professionals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    profession VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    locality VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
