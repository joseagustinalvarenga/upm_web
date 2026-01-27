CREATE TABLE features (
    id BIGSERIAL PRIMARY KEY,
    icon VARCHAR(100),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    display_order INTEGER
);
