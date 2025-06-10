-- Crear base de datos
CREATE DATABASE TusServiPrueba
CHARACTER SET utf8mb4 
COLLATE utf8mb4_spanish2_ci;

-- Activar base de datos
USE TusServiPrueba;

CREATE TABLE Usuarios (
    idUsuario        INT PRIMARY KEY AUTO_INCREMENT,
    nombreUsuario    VARCHAR(100) NOT NULL,
    emailUsuario     VARCHAR(150) UNIQUE NOT NULL,
    contraseñaUsuario VARCHAR(255) NOT NULL,
    telefonoUsuario  VARCHAR(15),
    direccionUsuario VARCHAR(250),
    ciudadUsuario           VARCHAR(100),
    tipoUsuario      ENUM('cliente', 'profesional') NOT NULL, -- Para distinguir cliente y profesional
    fechaRegistroUsuario   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE Usuarios
ADD COLUMN fotoPerfilUsuario TEXT;



SELECT * FROM Usuarios;
        
CREATE TABLE Profesionales (
    idProfesional   INT PRIMARY KEY AUTO_INCREMENT,
    idUsuarioFK       INT UNIQUE NOT NULL, -- Relacionado con Usuarios
    categoriaProfesional        VARCHAR(100) NOT NULL, -- Ejemplo: "Fontanero", "Electricista"
    descripcionProfesional      TEXT, -- Descripción del servicio
    experienciaProfesional      INT, -- Años de experiencia
    horarioProfesional          VARCHAR(100), -- Horario de disponibilidad
    ubicacionProfesional        VARCHAR(255), -- Áreas donde trabaja
    fotoPerfilProfesional      VARCHAR(255), -- URL de la foto de perfil
    redesSocialesProfesional   VARCHAR(255), -- Links a redes sociales o página web
    FOREIGN KEY (idUsuarioFK) REFERENCES Usuarios(idUsuario) ON DELETE CASCADE
);

ALTER TABLE profesionales DROP COLUMN horarioProfesional;
ALTER TABLE profesionales DROP COLUMN ubicacionProfesional;
ALTER TABLE profesionales DROP COLUMN descripcionProfesional;
ALTER TABLE profesionales DROP COLUMN redesSocialesProfesional;

SELECT * FROM Profesionales;



-- NUEVA TABLA: Empresas
CREATE TABLE Empresas (
    idEmpresa INT PRIMARY KEY AUTO_INCREMENT,
    idProfesionalFK INT NOT NULL,
    nombreEmpresa VARCHAR(100) NOT NULL,
    descripcionEmpresa TEXT,
    ubicacionEmpresa VARCHAR(255),
    horarioEmpresa VARCHAR(100),
    webEmpresa VARCHAR(255),
    logoEmpresa VARCHAR(255),
    fechaRegistroEmpresa TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idProfesionalFK) REFERENCES Profesionales(idProfesional) ON DELETE CASCADE
);
Select * from empresas;




CREATE TABLE Servicios (
    idServicio INT PRIMARY KEY AUTO_INCREMENT,
    idEmpresaFK INT NOT NULL,  -- Cambiado a Empresa
    tituloServicio VARCHAR(100) NOT NULL,
    descripcionServicio TEXT NOT NULL,
    precioEstimadoServicio DECIMAL(10,2),
    fechaPublicacionServicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idEmpresaFK) REFERENCES Empresas(idEmpresa) ON DELETE CASCADE
);
select * FROM Servicios;




CREATE TABLE Tareas (
    idTarea INT PRIMARY KEY AUTO_INCREMENT,
    idUsuario INT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    realizada BOOLEAN DEFAULT FALSE,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario) ON DELETE CASCADE
);
ALTER TABLE Tareas
ADD COLUMN horaInicio TIME NOT NULL DEFAULT '00:00:00',
ADD COLUMN horaFin TIME NOT NULL DEFAULT '00:00:00';

SELECT * FROM tareas;





CREATE TABLE mensajes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emisor_id INT NOT NULL,
    receptor_id INT NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE mensajes ADD COLUMN leido BOOLEAN DEFAULT FALSE;
ALTER TABLE mensajes 
ADD COLUMN tipo ENUM('texto', 'imagen', 'video') DEFAULT 'texto';

