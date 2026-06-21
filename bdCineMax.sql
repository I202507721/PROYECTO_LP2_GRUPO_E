CREATE DATABASE IF NOT EXISTS bd_cine;
USE bd_cine;

-- 1. MANTENIMIENTO 1: Películas
CREATE TABLE pelicula (
    id_pelicula INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    genero VARCHAR(50) NOT NULL,
    duracion INT NOT NULL, -- En minutos
    clasificacion VARCHAR(10) NOT NULL,
    estado TINYINT DEFAULT 1 -- 1: Activo, 0: Inactivo (Baja lógica)
);

-- 2. MANTENIMIENTO 2: Salas
CREATE TABLE sala (
    id_sala INT AUTO_INCREMENT PRIMARY KEY,
    numero_sala INT NOT NULL UNIQUE,
    capacidad INT NOT NULL,
    tipo_proyeccion VARCHAR(10) NOT NULL -- 2D, 3D, Prime
);

-- 3. MANTENIMIENTO 3: Funciones (Cruza Películas y Salas)
CREATE TABLE funcion (
    id_funcion INT AUTO_INCREMENT PRIMARY KEY,
    id_pelicula INT,
    id_sala INT,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    precio_entrada DECIMAL(10,2) NOT NULL,
    asientos_disponibles INT NOT NULL,
    FOREIGN KEY (id_pelicula) REFERENCES pelicula(id_pelicula),
    FOREIGN KEY (id_sala) REFERENCES sala(id_sala)
);

-- 4. USUARIOS (Para el Login de la Semana 6)
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    rol VARCHAR(20) NOT NULL -- 'Administrador', 'Taquillero'
);

-- =========================================================
-- TABLAS PARA EL CASO TRANSACCIONAL (SEMANA 7)
-- =========================================================

-- Cabecera de la Venta
CREATE TABLE venta_cabecera (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT, -- Quién atendió la venta
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_pagado DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- Detalle de la Venta
CREATE TABLE venta_detalle (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT,
    id_funcion INT,
    cantidad_entradas INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta_cabecera(id_venta),
    FOREIGN KEY (id_funcion) REFERENCES funcion(id_funcion)
);

-- Control de Asientos de la Función
CREATE TABLE asiento_ocupado (
    id_asiento INT AUTO_INCREMENT PRIMARY KEY,
    id_funcion INT,
    id_venta INT,
    codigo_asiento VARCHAR(5) NOT NULL, -- Ej: 'A1', 'B5'
    FOREIGN KEY (id_funcion) REFERENCES funcion(id_funcion),
    FOREIGN KEY (id_venta) REFERENCES venta_cabecera(id_venta)
);


-- -------------------------procedure--------------------------

DELIMITER $$

CREATE PROCEDURE sp_iniciar_sesion(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(100)
)
BEGIN
    -- Busca al usuario con las credenciales correctas
    SELECT id_usuario, nombre, rol 
    FROM usuario 
    WHERE username = p_username AND password = p_password;
END $$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE sp_consultar_cartelera_disponible()
BEGIN
    SELECT 
        f.id_funcion,
        p.titulo AS pelicula,
        p.genero,
        p.clasificacion,
        s.numero_sala,
        s.tipo_proyeccion,
        f.fecha,
        f.hora_inicio,
        f.precio_entrada,
        f.asientos_disponibles
    FROM funcion f
    INNER JOIN pelicula p ON f.id_pelicula = p.id_pelicula
    INNER JOIN sala s ON f.id_sala = s.id_sala
    WHERE p.estado = 1 AND f.fecha >= CURDATE()
    ORDER BY f.fecha ASC, f.hora_inicio ASC;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_registrar_venta_completa(
    IN p_id_usuario INT,
    IN p_id_funcion INT,
    IN p_cantidad_entradas INT,
    IN p_asientos_codigos VARCHAR(255), -- Códigos separados por comas, ej: 'A1,A2'
    OUT p_codigo_error INT, -- 0 = Éxito, 1 = Error de capacidad, 2 = Error general
    OUT p_mensaje VARCHAR(100)
)
BEGIN
    DECLARE v_asientos_libres INT;
    DECLARE v_precio_entrada DECIMAL(10,2);
    DECLARE v_subtotal DECIMAL(10,2);
    DECLARE v_id_venta INT;
    DECLARE v_asiento_individual VARCHAR(5);
    DECLARE v_posicion INT DEFAULT 1;
    
    -- Manejo de excepciones generales
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_codigo_error = 2;
        SET p_mensaje = 'Error interno en el servidor de base de datos.';
    END;

    START TRANSACTION;
    
    -- 1. Verificar disponibilidad y precio de la función de forma aislada (Lock)
    SELECT asientos_disponibles, precio_entrada 
    INTO v_asientos_libres, v_precio_entrada
    FROM funcion 
    WHERE id_funcion = p_id_funcion FOR UPDATE;
    
    IF v_asientos_libres < p_cantidad_entradas THEN
        SET p_codigo_error = 1;
        SET p_mensaje = 'Capacidad insuficiente para la función seleccionada.';
        ROLLBACK;
    ELSE
        -- Calcular montos
        SET v_subtotal = v_precio_entrada * p_cantidad_entradas;
        
        -- 2. Insertar en Cabecera de Venta
        INSERT INTO venta_cabecera (id_usuario, total_pagado)
        VALUES (p_id_usuario, v_subtotal);
        
        SET v_id_venta = LAST_INSERT_ID();
        
        -- 3. Insertar en Detalle de Venta
        INSERT INTO venta_detalle (id_venta, id_funcion, cantidad_entradas, subtotal)
        VALUES (v_id_venta, p_id_funcion, p_cantidad_entradas, v_subtotal);
        
        -- 4. Actualizar el aforo de la función
        UPDATE funcion 
        SET asientos_disponibles = asientos_disponibles - p_cantidad_entradas
        WHERE id_funcion = p_id_funcion;
        
        -- 5. Registrar los asientos individuales (Simulación de procesamiento de String)
        -- Nota: En producción real se suele iterar un JSON o un array según el motor.
        -- Este bucle procesa los códigos separados por comas de forma secuencial.
        WHILE CHAR_LENGTH(p_asientos_codigos) > 0 AND v_posicion > 0 DO
            SET v_posicion = LOCATE(',', p_asientos_codigos);
            IF v_posicion > 0 THEN
                SET v_asiento_individual = SUBSTRING(p_asientos_codigos, 1, v_posicion - 1);
                SET p_asientos_codigos = SUBSTRING(p_asientos_codigos, v_posicion + 1);
            ELSE
                SET v_asiento_individual = p_asientos_codigos;
                SET p_asientos_codigos = '';
            END IF;
            
            INSERT INTO asiento_ocupado (id_funcion, id_venta, codigo_asiento)
            VALUES (p_id_funcion, v_id_venta, TRIM(v_asiento_individual));
        END WHILE;
        
        COMMIT;
        SET p_codigo_error = 0;
        SET p_mensaje = 'Venta registrada con éxito.';
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_cancelar_venta(
    IN p_id_venta INT,
    OUT p_mensaje VARCHAR(100)
)
BEGIN
    DECLARE v_id_funcion INT;
    DECLARE v_cantidad INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_mensaje = 'No se pudo procesar la cancelación de la venta.';
    END;

    START TRANSACTION;
    
    -- Obtener datos del detalle para saber qué función y cuántos asientos devolver
    SELECT id_funcion, cantidad_entradas 
    INTO v_id_funcion, v_cantidad
    FROM venta_detalle 
    WHERE id_venta = p_id_venta;
    
    IF v_id_funcion IS NOT NULL THEN
        -- 1. Devolver los asientos a la función
        UPDATE funcion 
        SET asientos_disponibles = asientos_disponibles + v_cantidad
        WHERE id_funcion = v_id_funcion;
        
        -- 2. Eliminar asientos ocupados
        DELETE FROM asiento_ocupado WHERE id_venta = p_id_venta;
        
        -- 3. Eliminar detalle y cabecera
        DELETE FROM venta_detalle WHERE id_venta = p_id_venta;
        DELETE FROM venta_cabecera WHERE id_venta = p_id_venta;
        
        COMMIT;
        SET p_mensaje = 'Operación cancelada y asientos liberados con éxito.';
    ELSE
        SET p_mensaje = 'La venta especificada no existe.';
        ROLLBACK;
    END IF;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_reporte_taquilla_ingresos(
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE
)
BEGIN
    SELECT 
        DATE(vc.fecha_venta) AS fecha,
        COUNT(DISTINCT vc.id_venta) AS total_transacciones,
        SUM(vd.cantidad_entradas) AS total_tickets_vendidos,
        SUM(vc.total_pagado) AS total_recaudado
    FROM venta_cabecera vc
    INNER JOIN venta_detalle vd ON vc.id_venta = vd.id_venta
    WHERE DATE(vc.fecha_venta) BETWEEN p_fecha_inicio AND p_fecha_fin
    GROUP BY DATE(vc.fecha_venta)
    ORDER BY fecha DESC;
END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_reporte_ocupacion_salas()
BEGIN
    SELECT 
        f.id_funcion,
        p.titulo AS pelicula,
        s.numero_sala,
        s.capacidad AS capacidad_total,
        f.asientos_disponibles,
        (s.capacidad - f.asientos_disponibles) AS asientos_vendidos,
        ROUND(((s.capacidad - f.asientos_disponibles) / s.capacidad) * 100, 2) AS porcentaje_ocupacion
    FROM funcion f
    INNER JOIN pelicula p ON f.id_pelicula = p.id_pelicula
    INNER JOIN sala s ON f.id_sala = s.id_sala
    WHERE f.fecha = CURDATE()
    ORDER BY porcentaje_ocupacion DESC;
END $$

DELIMITER ;


-- -----------------------------insert-------------------------------------------

USE bd_cine;

-- ==========================================
-- 1. INSERCIÓN EN TABLA: usuario
-- ==========================================
-- Contraseñas de prueba simuladas (En producción real deben ir encriptadas con BCrypt/SHA2)
INSERT INTO usuario (username, password, nombre, rol) VALUES
('admin01', 'Admin@2026', 'Carlos Mendoza', 'Administrador'),
('taquilla01', 'Taquilla@2026', 'Ana Gómez', 'Taquillero'),
('taquilla02', 'Ventas@2026', 'Luis Torres', 'Taquillero');

-- ==========================================
-- 2. INSERCIÓN EN TABLA: pelicula
-- ==========================================
INSERT INTO pelicula (titulo, genero, clasificacion, duracion, estado) VALUES
('Sci-Fi Odyssey 2026', 'Ciencia Ficción', 'PG-13', 145, 1),
('Misterio en el Altiplano', 'Suspenso', 'Mayores 14', 118, 1),
('Aventuras Animadas: El Regreso', 'Animación', 'Apta Todos', 95, 1),
('Crónicas del Pasado', 'Drama / Historia', 'Mayores 18', 160, 1),
('Película Antigua Retirada', 'Comedia', 'Apta Todos', 90, 0); -- Estado 0 para probar filtros de cartelera

-- ==========================================
-- 3. INSERCIÓN EN TABLA: sala
-- ==========================================
INSERT INTO sala (numero_sala, capacidad, tipo_proyeccion) VALUES
(1, 100, '2D'),
(2, 80, '3D'),
(3, 40, 'Prime');

-- ==========================================
-- 4. INSERCIÓN EN TABLA: funcion
-- ==========================================
-- Se asume el uso de CURDATE() (Fecha actual del servidor) para probar inmediatamente el CU04 y CU08
INSERT INTO funcion (id_pelicula, id_sala, fecha, hora_inicio, precio_entrada, asientos_disponibles) VALUES
(1, 1, CURDATE(), '15:00:00', 15.00, 100), -- Función 1: Sala 1 (Capacidad 100)
(1, 2, CURDATE(), '19:30:00', 22.00, 80),  -- Función 2: Sala 2 (Capacidad 80)
(2, 3, CURDATE(), '21:00:00', 35.00, 40),  -- Función 3: Sala 3 (Capacidad 40)
(3, 1, CURDATE(), '11:00:00', 12.00, 100), -- Función 4: Sala 1 (Capacidad 100)
(4, 2, CURDATE() + INTERVAL 1 DAY, '18:00:00', 20.00, 80); -- Función mañana

-- ==========================================
-- 5. SIMULACIÓN DE HISTORIAL DE VENTAS (Para Reportes)
-- ==========================================
-- Transacción 1: El taquillero 'Ana' (id_usuario = 2) vende 2 entradas para la Función 1
INSERT INTO venta_cabecera (id_usuario, fecha_venta, total_pagado) 
VALUES (2, CURRENT_TIMESTAMP, 30.00);

SET @id_venta_1 = LAST_INSERT_ID();

INSERT INTO venta_detalle (id_venta, id_funcion, cantidad_entradas, subtotal)
VALUES (@id_venta_1, 1, 2, 30.00);

INSERT INTO asiento_ocupado (id_funcion, id_venta, codigo_asiento) VALUES
(1, @id_venta_1, 'A1'),
(1, @id_venta_1, 'A2');

-- Actualizar el aforo de la función tras esta venta manual de prueba
UPDATE funcion SET asientos_disponibles = asientos_disponibles - 2 WHERE id_funcion = 1;


-- Transacción 2: El taquillero 'Luis' (id_usuario = 3) vende 1 entrada VIP para la Función 3
INSERT INTO venta_cabecera (id_usuario, fecha_venta, total_pagado) 
VALUES (3, CURRENT_TIMESTAMP, 35.00);

SET @id_venta_2 = LAST_INSERT_ID();

INSERT INTO venta_detalle (id_venta, id_funcion, cantidad_entradas, subtotal)
VALUES (@id_venta_2, 3, 1, 35.00);

INSERT INTO asiento_ocupado (id_funcion, id_venta, codigo_asiento) VALUES
(3, @id_venta_2, 'P5');

-- Actualizar el aforo de la función tras esta venta manual de prueba
UPDATE funcion SET asientos_disponibles = asientos_disponibles - 1 WHERE id_funcion = 3;
