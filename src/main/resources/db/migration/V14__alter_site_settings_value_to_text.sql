-- Cambio de VARCHAR(255) a TEXT para permitir guardar imágenes en Base64
ALTER TABLE site_settings ALTER COLUMN setting_value TYPE TEXT;
