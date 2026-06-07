-- ============================================
-- SCRIPT DE INICIALIZACIÓN - CENTRO DE BUCEO
-- ============================================

CREATE DATABASE IF NOT EXISTS centro_buceo;
USE centro_buceo;

-- TABLA: usuarios
CREATE TABLE IF NOT EXISTS usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    dni VARCHAR(200) NOT NULL UNIQUE,
    fechaNacimiento DATE,
    username VARCHAR(50) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('EMPLEADO', 'ADMINISTRADOR'))
);

-- TABLA: clientes
CREATE TABLE IF NOT EXISTS clientes (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    fechaNacimiento DATE,
    telefono INT,
    email VARCHAR(100),
    telefonoUrgencia VARCHAR(20),
    certificacion VARCHAR(30),
    numeroSeguro VARCHAR(50),
    seguroHasta DATE,
    fechaExp DATE
);

-- TABLA: instructores
CREATE TABLE IF NOT EXISTS instructores (
    idInstructor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    fechaNacimiento DATE,
    telefono INT,
    email VARCHAR(100),
    telefonoUrgencia VARCHAR(20),
    certificacion VARCHAR(30)
);

-- TABLA: barcos
CREATE TABLE IF NOT EXISTS barcos (
    idBarco INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    capacidad INT NOT NULL
);

-- TABLA: inmersion (tabla padre)
CREATE TABLE IF NOT EXISTS inmersion (
    idInmersion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    certificacionMinima VARCHAR(30),
    plazasMax INT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    duracionMin INT,
    lugar VARCHAR(200),
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('BARCO', 'COSTA')),
    UNIQUE (idInmersion, nombre, tipo, lugar, plazasMax, precio, duracionMin, certificacionMinima)
);

-- TABLA: inmersion_barco (hereda de inmersion)
--CREATE TABLE IF NOT EXISTS inmersion_barco (
--    idInmersion INT PRIMARY KEY,
--    idBarco INT NOT NULL,
--    FOREIGN KEY (idInmersion) REFERENCES inmersion(idInmersion) ON DELETE CASCADE,
--    FOREIGN KEY (idBarco) REFERENCES barcos(idBarco) ON DELETE CASCADE
--);

-- TABLA: inmersion_costa (hereda de inmersion)
--CREATE TABLE IF NOT EXISTS inmersion_costa (
--  idInmersion INT PRIMARY KEY,
--  lugar VARCHAR(200) NOT NULL,
--  FOREIGN KEY (idInmersion) REFERENCES inmersion(idInmersion) ON DELETE CASCADE
--);

-- TABLA: especialidades
CREATE TABLE IF NOT EXISTS especialidades (
    idEspecialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- TABLA: reservas
CREATE TABLE IF NOT EXISTS reservas (
    idReserva INT AUTO_INCREMENT PRIMARY KEY,
    idInmersion INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    idInstructor INT,
    idBarco INT,
    lugar VARCHAR(200),
    UNIQUE (idInmersion, fecha, hora, idInstructor),
    FOREIGN KEY (idInmersion) REFERENCES inmersion(idInmersion) ON DELETE CASCADE,
    FOREIGN KEY (idInstructor) REFERENCES instructores(idInstructor) ON DELETE SET NULL,
    FOREIGN KEY (idBarco) REFERENCES barcos(idBarco) ON DELETE SET NULL
);

-- TABLA: reserva_clientes (relación N:M entre reservas y clientes)
CREATE TABLE IF NOT EXISTS reserva_clientes (
    idReserva INT NOT NULL,
    idCliente INT NOT NULL,
    PRIMARY KEY (idReserva, idCliente),
    FOREIGN KEY (idReserva) REFERENCES reservas(idReserva) ON DELETE CASCADE,
    FOREIGN KEY (idCliente) REFERENCES clientes(idCliente) ON DELETE CASCADE
);

-- TABLA: cliente_especialidad (relación N:M entre clientes y especialidades)
CREATE TABLE IF NOT EXISTS cliente_especialidad (
    idCliente INT NOT NULL,
    idEspecialidad INT NOT NULL,
    PRIMARY KEY (idCliente, idEspecialidad),
    FOREIGN KEY (idCliente) REFERENCES clientes(idCliente) ON DELETE CASCADE,
    FOREIGN KEY (idEspecialidad) REFERENCES especialidades(idEspecialidad) ON DELETE CASCADE
);

-- TABLA: instructor_especialidad (relación N:M entre instructores y especialidades)
CREATE TABLE IF NOT EXISTS instructor_especialidad (
    idInstructor INT NOT NULL,
    idEspecialidad INT NOT NULL,
    PRIMARY KEY (idInstructor, idEspecialidad),
    FOREIGN KEY (idInstructor) REFERENCES instructores(idInstructor) ON DELETE CASCADE,
    FOREIGN KEY (idEspecialidad) REFERENCES especialidades(idEspecialidad) ON DELETE CASCADE
);

-- ============================================
-- DATOS DE PRUEBA
-- ============================================

-- Especialidades
INSERT IGNORE INTO especialidades (nombre) VALUES ('PROFUNDO'), ('APNEA'), ('CORRIENTES'), ('PECIOS'), ('CUEVAS');

-- Usuarios (contraseñas en Base64: "admin123" -> YWRtaW4xMjM=, "recepcion" -> cmVjZXBjaW9u)
INSERT IGNORE INTO usuario (nombre, apellidos, email, dni, fechaNacimiento, telefono, username, passwordHash, rol) VALUES
('Administrador Principal','', 'admin@centrobuceo.com', '16923451F', '1990-03-20', '600111222', 'admin', 'YWRtaW4xMjM=', 'ADMINISTRADOR'),
('María', 'López', 'maria@centrobuceo.com', '98978956F', '1980-03-20','600333444', 'maria', 'bWFyaWExMjM=', 'EMPLEADO');

-- Clientes
INSERT IGNORE INTO clientes (nombre, apellidos, dni, fechaNacimiento, telefono, email, telefonoUrgencia, certificacion, numeroSeguro, seguroHasta, fechaExp) VALUES
('Carlos', 'García Ruiz', '12345678A', '1990-05-15', 600111333, 'carlos@email.com', '600111334', 'OWD', 'SEGURO001', '2026-12-31', '2025-12-31'),
('Ana', 'Martínez López', '87654321B', '1985-08-22', 600222444, 'ana@email.com', '600222445', 'AOWD', 'SEGURO002', '2026-12-31', '2025-12-31'),
('Pedro', 'Sánchez Gómez', '11111111C', '1995-02-10', 600333555, 'pedro@email.com', '600333556', 'RESCUE', 'SEGURO003', '2026-12-31', '2025-12-31'),
('Laura', 'Fernández Díaz', '22222222D', '2000-11-30', 600444666, 'laura@email.com', '600444667', 'SCUBA', 'SEGURO004', '2026-12-31', '2025-12-31'),
('Javier', 'Rodríguez Pérez', '33333333E', '1988-07-05', 600555777, 'javier@email.com', '600555778', 'MASTERSCUBA', 'SEGURO005', '2026-12-31', '2025-12-31');

-- Instructores
INSERT IGNORE INTO instructores (nombre, apellidos, dni, fechaNacimiento, telefono, email, telefonoUrgencia, certificacion) VALUES
('Roberto', 'Díaz Martín', '44444444F', '1980-03-20', 600666888, 'roberto@centrobuceo.com', '600666889', 'OWSI'),
('Elena', 'Torres Ruiz', '55555555G', '1985-09-12', 600777999, 'elena@centrobuceo.com', '600777990', 'DIVEMASTER');

-- Barcos
INSERT IGNORE INTO barcos (nombre, capacidad) VALUES ('Neptuno I', 12), ('Neptuno II', 8), ('Poseidón I', 10), ('Mar Menor', 13);

-- Inmersiones (BARCO)
INSERT IGNORE INTO inmersion (nombre, certificacionMinima, plazasMax, precio, duracionMin, tipo) VALUES
('Bautismo en Barco', 'SCUBA', 10, 45.00, 30, 'BARCO'),
('Inmersión Arrecife', 'OWD', 8, 55.00, 45, 'BARCO'),
('El Naranjito', 'AOWD', 10, 60.00, 50, 'BARCO');

-- Inmersiones (COSTA)
INSERT IGNORE INTO inmersion (nombre, certificacionMinima, plazasMax, precio, duracionMin, tipo, lugar) VALUES
('Calas Escondidas', 'SCUBA', 6, 35.00, 60, 'COSTA', 'Cala del Bosque'),
('Fondo Marino', 'AOWD', 4, 40.00, 50, 'COSTA', 'Playa del Faro'),
('La Llana', 'SCUBA', 8, 30.00, 45, 'COSTA', 'La Llana'),
('Isla Perdiguera', 'OWD', 6, 42.00, 50, 'COSTA', 'Mar Menor');

-- Reservas (ejemplos)
INSERT IGNORE INTO reservas (idInmersion, fecha, hora, idInstructor, idBarco) VALUES
(1, CURDATE() + INTERVAL 1 DAY, '10:00:00', 1, 1),
(2, CURDATE() + INTERVAL 2 DAY, '15:00:00', 2, 2);
INSERT IGNORE INTO reservas (idInmersion, fecha, hora, idInstructor, idBarco) VALUES
(3, CURDATE() + INTERVAL 3 DAY, '09:30:00', 1, NULL),
(4, CURDATE() + INTERVAL 4 DAY, '11:00:00', 2, NULL);

-- Clientes en reservas
INSERT IGNORE INTO reserva_clientes (idReserva, idCliente) VALUES (1, 1), (1, 2), (2, 3);