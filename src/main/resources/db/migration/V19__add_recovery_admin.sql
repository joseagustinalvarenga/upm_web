-- Independent administrator account. Store only the BCrypt hash here.
-- Preserve its password and status if this username already exists.
INSERT INTO users (username, password_hash, role, enabled)
SELECT 'admin_upm', '$2a$10$gy0lGMtPaNtnhtFhFsHdBeUtL5sH27scOK66u2OLCmae949Z5BSBC', 'ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin_upm');
