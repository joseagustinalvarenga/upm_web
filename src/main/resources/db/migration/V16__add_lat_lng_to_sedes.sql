ALTER TABLE sedes ADD COLUMN latitude DOUBLE PRECISION;
ALTER TABLE sedes ADD COLUMN longitude DOUBLE PRECISION;

-- Coordenadas predeterminadas de las sedes
UPDATE sedes SET latitude = -27.36708, longitude = -55.89608 WHERE name = 'Sede Central - Posadas';
UPDATE sedes SET latitude = -27.48333, longitude = -55.11667 WHERE name = 'Sede Oberá';
UPDATE sedes SET latitude = -26.40694, longitude = -54.62917 WHERE name = 'Sede Eldorado';
UPDATE sedes SET latitude = -25.59912, longitude = -54.57355 WHERE name = 'Sede Puerto Iguazú';
