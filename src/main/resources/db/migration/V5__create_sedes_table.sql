CREATE TABLE sedes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(255),
    schedule VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    map_url VARCHAR(500),
    image_url VARCHAR(255)
);

INSERT INTO sedes (name, description, address, schedule, phone, email, map_url) VALUES 
('Sede Central - Posadas', 'Nuestra sede principal cuenta con modernas aulas, biblioteca central y laboratorios de informática.', 'Av. Corrientes 1234, Posadas', 'Lun a Vie: 8:00 - 21:00 hs', '(376) 444-0001', 'posadas@upm.edu.ar', '#'),
('Sede Oberá', 'Ubicada en la Capital del Monte, ofrece carreras orientadas a la tecnología y el arte.', 'Calle Sarmiento 500, Oberá', 'Lun a Vie: 8:00 - 20:00 hs', '(3755) 42-0002', 'obera@upm.edu.ar', '#'),
('Sede Eldorado', 'Especializada en carreras forestales y ambientales, en pleno corazón del Alto Paraná.', 'Av. San Martín 2000, Km 9, Eldorado', 'Lun a Vie: 7:00 - 19:00 hs', '(3751) 43-0003', 'eldorado@upm.edu.ar', '#'),
('Sede Puerto Iguazú', 'Con foco en turismo y hotelería, cerca de una de las maravillas naturales del mundo.', 'Av. Victoria Aguirre 550, Iguazú', 'Lun a Vie: 9:00 - 18:00 hs', '(3757) 42-0004', 'iguazu@upm.edu.ar', '#');
