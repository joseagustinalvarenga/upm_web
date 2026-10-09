-- Independent administrator account. Store only the BCrypt hash here.
-- Preserve its password and status if this username already exists.
INSERT INTO users (username, password_hash, role, enabled)
SELECT 'admin_upm', '$2a$10$1ZkUwB7kpz30n1nuAG9pZOPuEMeAyKmpkkkPBNwhQmBlvu280/2Ea', 'ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin_upm');
