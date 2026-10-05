CREATE TABLE professional_applications (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    dni VARCHAR(50),
    profession VARCHAR(255) NOT NULL,
    phone VARCHAR(100),
    email VARCHAR(255) NOT NULL,
    locality VARCHAR(100),
    course_completed VARCHAR(255),
    notes TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
