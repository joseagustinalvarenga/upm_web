CREATE TABLE site_settings (
    setting_key VARCHAR(255) NOT NULL PRIMARY KEY,
    setting_value VARCHAR(255)
);

INSERT INTO site_settings (setting_key, setting_value) 
VALUES ('home_hero_image_url', '/images/university_students_hero.png');
