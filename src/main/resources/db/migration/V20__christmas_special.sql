CREATE TABLE christmas_videos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(140) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(60) NOT NULL,
    video_url VARCHAR(1000) NOT NULL,
    cover TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    visible BOOLEAN NOT NULL DEFAULT TRUE
);
INSERT INTO site_settings (setting_key, setting_value) VALUES ('christmas_enabled', 'false');
