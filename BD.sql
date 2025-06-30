CREATE DATABASE sagadis2025;
USE sagadis2025;


-- Tabla Horarios de Trabajador
CREATE TABLE tb_horario (
	id_horario INT AUTO_INCREMENT PRIMARY KEY,
	descripcion VARCHAR(250),
	hora_inicio TIME,
	hora_fin TIME,
	tolerancia_ingreso TINYINT,
	tolerancia_salida TINYINT,
	estado BOOLEAN,
	fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
	fecha_modificacion DATETIME
);

-- Tabla Trabajador
CREATE TABLE tb_trabajador (
	id_trabajador INT AUTO_INCREMENT PRIMARY KEY,
	id_horario INT,
	nombres VARCHAR(50),
	apellidos VARCHAR(50),
	fecha_nacimiento DATE,
	numero_documento VARCHAR(20),
	telefono VARCHAR(20),
	correo_electronico VARCHAR(100),
	cargo VARCHAR(100),
	area VARCHAR(100),
	estado BOOLEAN,
	fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
	fecha_modificacion DATETIME,
	FOREIGN KEY (id_horario) REFERENCES tb_horario(id_horario)
);

-- Tabla Rol del Trabajador
CREATE TABLE tb_rol (
	id_rol INT AUTO_INCREMENT PRIMARY KEY,
	descripcion VARCHAR(60),
	estado BOOLEAN,
	fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
	fecha_modificacion DATETIME
);

-- Tabla Usuarios
CREATE TABLE tb_usuario (
	id_usuario INT AUTO_INCREMENT PRIMARY KEY,
	id_rol INT,
    id_trabajador INT UNIQUE,
	username VARCHAR(30),
	contrasenia VARCHAR(60),
	estado BOOLEAN,
	fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
	fecha_modificacion DATETIME,
	FOREIGN KEY (id_rol) REFERENCES tb_rol(id_rol),
    FOREIGN KEY (id_trabajador) REFERENCES tb_trabajador(id_trabajador)
);

-- Tabla Asistencia del Trabajador
CREATE TABLE tb_asistencia (
	id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
	id_trabajador INT,
	fecha DATE,
	hora TIME,
	tipo_marca CHAR(3),
    desc_marc CHAR(3),
	estado BOOLEAN,
	fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
	fecha_modificacion DATETIME,
	FOREIGN KEY (id_trabajador) REFERENCES tb_trabajador(id_trabajador)
);


INSERT INTO tb_horario (descripcion, hora_inicio, hora_fin, tolerancia_ingreso, tolerancia_salida, estado)
VALUES 
('Horario Regular - Oficina', '08:00:00', '17:00:00', 10, 5, TRUE),
('Horario Medio Tiempo', '09:00:00', '13:00:00', 5, 5, TRUE);

INSERT INTO tb_rol (descripcion, estado)
VALUES 
('Administrador', TRUE),
('Trabajador', TRUE);

INSERT INTO tb_trabajador (id_horario, nombres, apellidos, fecha_nacimiento, numero_documento, telefono, correo_electronico, cargo, area, estado)
VALUES 

(1, 'Juan', 'Pérez', '1990-05-12', '12345678', '987654321', 'juan.perez@mail.com', 'Analista', 'Sistemas', TRUE),
(2, 'María', 'Ramírez', '1988-11-30', '87654321', '987654322', 'maria.ramirez@mail.com', 'Asistente', 'Recursos Humanos', TRUE), 
(2, 'Roy', 'Ochoa', '2004-01-26', '74354575', '987654322', 'roy.ochoa@mail.com', 'Asistente', 'TI', TRUE),
(2, 'Jesus', 'Chavez', '2004-02-14', '75236515', '987654322', 'jesus.chavez@mail.com', 'Analista', 'TI', TRUE);

INSERT INTO tb_usuario (id_rol, id_trabajador, username, contrasenia, estado)
VALUES 
(1, 1, 'admin001', '123456', TRUE),
(2, 2, 'trab001', 'abc123', TRUE);

INSERT INTO tb_asistencia (id_trabajador, fecha, hora, tipo_marca, estado)
VALUES 
(1, CURDATE(), CURTIME(), 'ENT', TRUE),
(1, CURDATE(), ADDTIME(CURTIME(), '08:00:00'), 'SAL', TRUE),
(2, CURDATE(), CURTIME(), 'ENT', TRUE);






DELIMITER $$

CREATE PROCEDURE spU_Registrar_Marcacion_Mensaje (
    IN p_id_trabajador INT,
    OUT p_mensaje VARCHAR(100)
)
BEGIN
    DECLARE v_hora_marcacion DATETIME DEFAULT NOW();
    DECLARE v_hora_inicio TIME;
    DECLARE v_hora_fin TIME;
    DECLARE v_tolerancia_ingreso TINYINT;
    DECLARE v_tolerancia_salida TINYINT;
    DECLARE v_hora_max_ing TIME;
    DECLARE v_hora_limite_ingreso TIME;
    DECLARE v_hora_limite_salida TIME;
    DECLARE v_tiene_entrada INT DEFAULT 0;
    DECLARE v_tiene_salida INT DEFAULT 0;

    -- Obtener datos del horario del trabajador
    SELECT 
        h.hora_inicio, 
        h.hora_fin, 
        h.tolerancia_ingreso, 
        h.tolerancia_salida
    INTO 
        v_hora_inicio, 
        v_hora_fin, 
        v_tolerancia_ingreso, 
        v_tolerancia_salida
    FROM tb_trabajador t
    INNER JOIN tb_horario h ON t.id_horario = h.id_horario
    WHERE t.id_trabajador = p_id_trabajador;

    -- Calcular límites
    SET v_hora_max_ing = ADDTIME(v_hora_inicio, CONCAT(v_tolerancia_ingreso, ':00'));
    SET v_hora_limite_ingreso = ADDTIME(v_hora_inicio, '01:00:00');
    SET v_hora_limite_salida = ADDTIME(v_hora_fin, CONCAT(v_tolerancia_salida, ':00'));

    -- Verificar si ya existe entrada o salida registrada
    SELECT COUNT(*) INTO v_tiene_entrada
    FROM tb_asistencia
    WHERE id_trabajador = p_id_trabajador
      AND fecha = CURDATE()
      AND tipo_marca = 'ENT';

    SELECT COUNT(*) INTO v_tiene_salida
    FROM tb_asistencia
    WHERE id_trabajador = p_id_trabajador
      AND fecha = CURDATE()
      AND tipo_marca = 'SAL';

    -- Evaluar hora de marcación
    IF TIME(v_hora_marcacion) < v_hora_inicio THEN
        SET p_mensaje = 'NO PUEDE MARCAR ANTES DE LA HORA DE ENTRADA';

    ELSEIF TIME(v_hora_marcacion) <= v_hora_limite_salida THEN
        -- Dentro del rango permitido

        IF v_tiene_entrada = 0 THEN
            -- Registrar entrada
            IF TIME(v_hora_marcacion) <= v_hora_max_ing THEN
                INSERT INTO tb_asistencia (
                    id_trabajador, fecha, hora, tipo_marca, desc_marc, estado, fecha_creacion, fecha_modificacion
                ) VALUES (
                    p_id_trabajador, CURDATE(), TIME(v_hora_marcacion), 'ENT', 'PUN', 1, NOW(), NULL
                );
                SET p_mensaje = 'MARCACIÓN DE ENTRADA REGISTRADA (PUNTUAL)';
            ELSE
                INSERT INTO tb_asistencia (
                    id_trabajador, fecha, hora, tipo_marca, desc_marc, estado, fecha_creacion, fecha_modificacion
                ) VALUES (
                    p_id_trabajador, CURDATE(), TIME(v_hora_marcacion), 'ENT', 'TAR', 1, NOW(), NULL
                );
                SET p_mensaje = 'MARCACIÓN DE ENTRADA REGISTRADA (TARDANZA)';
            END IF;

        ELSEIF v_tiene_salida = 0 THEN
            -- Registrar salida
            INSERT INTO tb_asistencia (
                id_trabajador, fecha, hora, tipo_marca, desc_marc, estado, fecha_creacion, fecha_modificacion
            ) VALUES (
                p_id_trabajador, CURDATE(), TIME(v_hora_marcacion), 'SAL', 'RET', 1, NOW(), NULL
            );
            SET p_mensaje = 'MARCACIÓN DE SALIDA REGISTRADA';

        ELSE
            SET p_mensaje = 'YA REGISTRÓ SU ENTRADA Y SALIDA DE HOY';
        END IF;

    ELSE
        -- Fuera del horario
        IF v_tiene_salida = 0 THEN
            INSERT INTO tb_asistencia (
                id_trabajador, fecha, hora, tipo_marca, desc_marc, estado, fecha_creacion, fecha_modificacion
            ) VALUES (
                p_id_trabajador, CURDATE(), TIME(v_hora_marcacion), 'SAL', 'FUH', 1, NOW(), NULL
            );
            SET p_mensaje = 'MARCACIÓN DE SALIDA FUERA DE HORARIO REGISTRADA';
        ELSE
            SET p_mensaje = 'YA REGISTRÓ SU SALIDA HOY';
        END IF;
    END IF;
END$$

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE spU_Listado_Usuario_Activo (
    IN p_id_usuario INT
)
BEGIN
    UPDATE tb_usuario
    SET estado = FALSE,
        fecha_modificacion = NOW()
    WHERE id_usuario = p_id_usuario;
END$$

DELIMITER ;





DELIMITER $$

CREATE PROCEDURE spU_Eliminar_Usuario (
    IN p_id_usuario INT
)
BEGIN
    UPDATE tb_usuario
    SET estado = FALSE,
        fecha_modificacion = NOW()
    WHERE id_usuario = p_id_usuario;
END$$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE spU_Eliminar_Trabajador (
    IN p_id_trabajador INT
)
BEGIN
    UPDATE tb_trabajador
    SET estado = FALSE,
        fecha_modificacion = NOW()
    WHERE id_trabajador = p_id_trabajador;
END$$

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE spU_Eliminar_Horario (
    IN p_id_horario INT
)
BEGIN
    UPDATE tb_horario
    SET estado = FALSE,
        fecha_modificacion = NOW()
    WHERE id_horario = p_id_horario;
END$$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE sp_Listado_Trabajador_ActivoSinUsuario()
BEGIN
    SELECT 
*
    FROM 
        tb_trabajador t
    WHERE 
        t.estado = TRUE
        AND t.id_trabajador NOT IN (
            SELECT u.id_trabajador FROM tb_usuario u
        );
END$$

DELIMITER ;

SELECT * FROM tb_trabajador;

SELECT * FROM tb_usuario;
SELECT * FROM tb_asistencia;
SELECT * FROM tb_horario;
