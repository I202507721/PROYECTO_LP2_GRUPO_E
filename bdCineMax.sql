CREATE DATABASE IF NOT EXISTS `bd_cine` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bd_cine`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ==========================================
-- 1. ESTRUCTURA Y DATOS: tipo_usuario
-- ==========================================
DROP TABLE IF EXISTS `tipo_usuario`;
CREATE TABLE `tipo_usuario` (
  `id_tipo` int NOT NULL,
  `descripcion` varchar(20) NOT NULL,
  PRIMARY KEY (`id_tipo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `tipo_usuario` WRITE;
/*!40000 ALTER TABLE `tipo_usuario` DISABLE KEYS */;
INSERT INTO `tipo_usuario` VALUES (1,'Administrador'),(2,'Taquillero');
/*!40000 ALTER TABLE `tipo_usuario` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- 2. ESTRUCTURA Y DATOS: usuario
-- ==========================================
DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `activo` bit(1) DEFAULT b'1',
  `id_tipo` int DEFAULT '2',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_usuario_tipo` (`id_tipo`),
  CONSTRAINT `fk_usuario_tipo` FOREIGN KEY (`id_tipo`) REFERENCES `tipo_usuario` (`id_tipo`),
  CONSTRAINT `chk_tipo_usuario` CHECK ((`id_tipo` in (1,2)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES 
(1,'admin01','Admin@2026','Carlos Mendoza',_binary '',1),
(2,'taquilla01','Taquilla@2026','Ana Gómez',_binary '',2),
(3,'taquilla02','Ventas@2026','Luis Torres',_binary '',2);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- 3. ESTRUCTURA Y DATOS: genero
-- ==========================================
DROP TABLE IF EXISTS `genero`;
CREATE TABLE `genero` (
  `id_genero` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(50) NOT NULL,
  PRIMARY KEY (`id_genero`),
  UNIQUE KEY `uk_genero_descripcion` (`descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `genero` WRITE;
/*!40000 ALTER TABLE `genero` DISABLE KEYS */;
INSERT INTO `genero` VALUES 
(1,'Acción'),
(4,'Animación'),
(2,'Ciencia Ficción'),
(6,'Comedia'),
(5,'Drama / Historia'),
(3,'Suspenso');
/*!40000 ALTER TABLE `genero` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- 4. ESTRUCTURA Y DATOS: pelicula
-- ==========================================
DROP TABLE IF EXISTS `pelicula`;
CREATE TABLE `pelicula` (
  `id_pelicula` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(100) NOT NULL,
  `id_genero` int NOT NULL,
  `duracion` int NOT NULL,
  `clasificacion` varchar(10) NOT NULL,
  `estado` bit(1) DEFAULT b'1',
  PRIMARY KEY (`id_pelicula`),
  UNIQUE KEY `uk_pelicula_titulo` (`titulo`),
  KEY `fk_pelicula_genero` (`id_genero`),
  CONSTRAINT `fk_pelicula_genero` FOREIGN KEY (`id_genero`) REFERENCES `genero` (`id_genero`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `pelicula` WRITE;
/*!40000 ALTER TABLE `pelicula` DISABLE KEYS */;
INSERT INTO `pelicula` VALUES 
(1,'Sci-Fi Odyssey 2026',2,145,'PG-13',_binary ''),
(2,'Misterio en el Altiplano',3,118,'Mayores 14',_binary ''),
(3,'Aventuras Animadas: El Regreso',4,95,'Apta Todos',_binary ''),
(4,'Crónicas del Pasado',5,160,'Mayores 18',_binary ''),
(5,'Película Antigua Retirada',6,90,'Apta Todos',_binary '\0');
/*!40000 ALTER TABLE `pelicula` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- 5. ESTRUCTURA Y DATOS: sala
-- ==========================================
DROP TABLE IF EXISTS `sala`;
CREATE TABLE `sala` (
  `id_sala` int NOT NULL AUTO_INCREMENT,
  `numero_sala` int NOT NULL,
  `capacidad` int NOT NULL,
  `tipo_proyeccion` varchar(10) NOT NULL,
  `precio_base` decimal(10,2) NOT NULL DEFAULT '12.50',
  PRIMARY KEY (`id_sala`),
  UNIQUE KEY `numero_sala` (`numero_sala`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `sala` WRITE;
/*!40000 ALTER TABLE `sala` DISABLE KEYS */;
INSERT INTO `sala` VALUES 
(1,1,100,'2D',12.50),
(2,2,100,'2D',12.50),
(3,3,100,'2D',12.50),
(4,4,100,'2D',12.50),
(5,5,100,'2D',12.50),
(6,6,100,'2D',12.50),
(7,7,100,'2D',12.50),
(8,8,100,'2D',12.50),
(9,9,100,'2D',12.50),
(10,10,80,'3D',17.00),
(11,11,80,'3D',17.00),
(12,12,120,'3D XL',21.90);
/*!40000 ALTER TABLE `sala` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- 6. ESTRUCTURA Y DATOS: funcion
-- ==========================================
DROP TABLE IF EXISTS `funcion`;
CREATE TABLE `funcion` (
  `id_funcion` int NOT NULL AUTO_INCREMENT,
  `id_pelicula` int DEFAULT NULL,
  `id_sala` int DEFAULT NULL,
  `fecha` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `precio_entrada` decimal(10,2) NOT NULL,
  `asientos_disponibles` int NOT NULL,
  PRIMARY KEY (`id_funcion`),
  KEY `fk_funcion_pelicula` (`id_pelicula`),
  KEY `fk_funcion_sala` (`id_sala`),
  CONSTRAINT `fk_funcion_pelicula` FOREIGN KEY (`id_pelicula`) REFERENCES `pelicula` (`id_pelicula`),
  CONSTRAINT `fk_funcion_sala` FOREIGN KEY (`id_sala`) REFERENCES `sala` (`id_sala`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `funcion` WRITE;
/*!40000 ALTER TABLE `funcion` DISABLE KEYS */;
INSERT INTO `funcion` VALUES 
(1,1,1,CURDATE(),'15:00:00',12.50,98),   
(2,1,10,CURDATE(),'19:30:00',17.00,80),  
(3,2,12,CURDATE(),'21:00:00',21.90,119), 
(4,3,2,CURDATE(),'11:00:00',12.50,100),  
(5,4,3,DATE_ADD(CURDATE(), INTERVAL 1 DAY),'18:00:00',12.50,100);
/*!40000 ALTER TABLE `funcion` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- 7. ESTRUCTURA Y DATOS: venta_cabecera
-- ==========================================
DROP TABLE IF EXISTS `venta_cabecera`;
CREATE TABLE `venta_cabecera` (
  `id_venta` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `fecha_venta` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total_pagado` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_venta`),
  KEY `fk_venta_usuario` (`id_usuario`),
  CONSTRAINT `fk_venta_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `venta_cabecera` WRITE;
/*!40000 ALTER TABLE `venta_cabecera` DISABLE KEYS */;
INSERT INTO `venta_cabecera` VALUES (1,2,CURRENT_TIMESTAMP,25.00),(2,3,CURRENT_TIMESTAMP,21.90);
/*!40000 ALTER TABLE `venta_cabecera` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- 8. ESTRUCTURA Y DATOS: venta_detalle
-- ==========================================
DROP TABLE IF EXISTS `venta_detalle`;
CREATE TABLE `venta_detalle` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_venta` int DEFAULT NULL,
  `id_funcion` int DEFAULT NULL,
  `cantidad_entradas` int NOT NULL, 
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `fk_detalle_venta` (`id_venta`),
  KEY `fk_detalle_funcion` (`id_funcion`),
  CONSTRAINT `fk_detalle_funcion` FOREIGN KEY (`id_funcion`) REFERENCES `funcion` (`id_funcion`),
  CONSTRAINT `fk_detalle_venta` FOREIGN KEY (`id_venta`) REFERENCES `venta_cabecera` (`id_venta`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `venta_detalle` WRITE;
/*!40000 ALTER TABLE `venta_detalle` DISABLE KEYS */;
INSERT INTO `venta_detalle` VALUES (1,1,1,2,25.00),(2,2,3,1,21.90);
/*!40000 ALTER TABLE `venta_detalle` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- 9. ESTRUCTURA Y DATOS: asiento_ocupado
-- ==========================================
DROP TABLE IF EXISTS `asiento_ocupado`;
CREATE TABLE `asiento_ocupado` (
  `id_asiento` int NOT NULL AUTO_INCREMENT,
  `id_funcion` int DEFAULT NULL,
  `id_venta` int DEFAULT NULL,
  `codigo_asiento` varchar(5) NOT NULL,
  PRIMARY KEY (`id_asiento`),
  KEY `fk_asiento_funcion` (`id_funcion`),
  KEY `fk_asiento_venta` (`id_venta`),
  CONSTRAINT `fk_asiento_funcion` FOREIGN KEY (`id_funcion`) REFERENCES `funcion` (`id_funcion`),
  CONSTRAINT `fk_asiento_venta` FOREIGN KEY (`id_venta`) REFERENCES `venta_cabecera` (`id_venta`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `asiento_ocupado` WRITE;
/*!40000 ALTER TABLE `asiento_ocupado` DISABLE KEYS */;
INSERT INTO `asiento_ocupado` VALUES (1,1,1,'A1'),(2,1,1,'A2'),(3,3,2,'P5');
/*!40000 ALTER TABLE `asiento_ocupado` ENABLE KEYS */;
UNLOCK TABLES;

-- ==========================================
-- VISTAS (VIEWS)
-- ==========================================
DROP VIEW IF EXISTS `v_header_venta`;
CREATE VIEW `v_header_venta` AS 
SELECT 
  `vc`.`id_venta` AS `num_venta`,
  CONCAT('V001 - ', LPAD(`vc`.`id_venta`, 8, '0')) AS `numVentaText`,
  CONCAT(`u`.`nombre`, ' (', `tu`.`descripcion`, ')') AS `usuarioTaquilla`,
  DATE_FORMAT(`vc`.`fecha_venta`, '%d/%m/%Y %h:%i:%s %p') AS `fechaText`
FROM `venta_cabecera` `vc`
JOIN `usuario` `u` ON `vc`.`id_usuario` = `u`.`id_usuario`
JOIN `tipo_usuario` `tu` ON `u`.`id_tipo` = `tu`.`id_tipo`;

DROP VIEW IF EXISTS `v_detail_venta`;
CREATE VIEW `v_detail_venta` AS 
SELECT 
  `vd`.`id_venta` AS `num_venta`,
  `p`.`titulo` AS `pelicula`,
  `s`.`numero_sala` AS `sala`,
  `vd`.`cantidad_entradas` AS `cantidad`, 
  (`vd`.`subtotal` / `vd`.`cantidad_entradas`) AS `precio_unitario`, 
  `vd`.`subtotal` AS `sub_total`
FROM `venta_detalle` `vd`
JOIN `funcion` `f` ON `vd`.`id_funcion` = `f`.`id_funcion`
JOIN `pelicula` `p` ON `f`.`id_pelicula` = `p`.`id_pelicula`
JOIN `sala` `s` ON `f`.`id_sala` = `s`.`id_sala`;

-- ==========================================
-- PROCEDIMIENTOS ALMACENADOS (STORED PROCEDURES)
-- ==========================================
DELIMITER $$
DROP PROCEDURE IF EXISTS `sp_iniciar_sesion`$$
CREATE PROCEDURE `sp_iniciar_sesion`(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(100)
)
BEGIN
    SELECT u.id_usuario, u.nombre, tu.descripcion AS rol, u.activo 
    FROM usuario u
    INNER JOIN tipo_usuario tu ON u.id_tipo = tu.id_tipo
    WHERE u.username = p_username AND u.password = p_password;
END $$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS `sp_consultar_cartelera_disponible`$$
CREATE PROCEDURE `sp_consultar_cartelera_disponible`()
BEGIN
    SELECT 
        f.id_funcion, p.titulo AS pelicula, g.descripcion AS genero, p.clasificacion,
        s.numero_sala, s.tipo_proyeccion, f.fecha, f.hora_inicio,
        f.precio_entrada, f.asientos_disponibles
    FROM funcion f
    INNER JOIN pelicula p ON f.id_pelicula = p.id_pelicula
    INNER JOIN genero g ON p.id_genero = g.id_genero
    INNER JOIN sala s ON f.id_sala = s.id_sala
    WHERE p.estado = 1 AND f.fecha >= CURDATE()
    ORDER BY f.fecha ASC, f.hora_inicio ASC;
END $$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS `sp_registrar_venta_completa`$$
CREATE PROCEDURE `sp_registrar_venta_completa`(
    IN p_id_usuario INT,
    IN p_id_funcion INT,
    IN p_cantidad_entradas INT,
    IN p_asientos_codigos VARCHAR(255)
)
BEGIN
    DECLARE v_asientos_libres INT;
    DECLARE v_precio_entrada DECIMAL(10,2);
    DECLARE v_subtotal DECIMAL(10,2);
    DECLARE v_id_venta INT;
    DECLARE v_asiento_individual VARCHAR(5);
    DECLARE v_posicion INT DEFAULT 1;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error interno en el servidor de base de datos.';
    END;

    START TRANSACTION;
    
    SELECT asientos_disponibles, precio_entrada 
    INTO v_asientos_libres, v_precio_entrada
    FROM funcion 
    WHERE id_funcion = p_id_funcion FOR UPDATE;
    
    IF v_asientos_libres < p_cantidad_entradas THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Capacidad insuficiente para la función seleccionada.';
    ELSE
        SET v_subtotal = v_precio_entrada * p_cantidad_entradas;
        
        INSERT INTO venta_cabecera (id_usuario, total_pagado) 
        VALUES (p_id_usuario, v_subtotal);
        
        SET v_id_venta = LAST_INSERT_ID();
        
        INSERT INTO venta_detalle (id_venta, id_funcion, cantidad_entradas, subtotal)
        VALUES (v_id_venta, p_id_funcion, p_cantidad_entradas, v_subtotal);
        
        UPDATE funcion 
        SET asientos_disponibles = asientos_disponibles - p_cantidad_entradas
        WHERE id_funcion = p_id_funcion;
        
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
    END IF;
END $$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS `sp_filtrar_pelicula_por_genero`$$
CREATE PROCEDURE `sp_filtrar_pelicula_por_genero`(IN p_genero VARCHAR(50))
BEGIN
    SELECT p.id_pelicula, p.titulo, g.descripcion as genero, p.duracion, p.clasificacion, p.estado
    FROM pelicula p
    INNER JOIN genero g ON p.id_genero = g.id_genero
    WHERE g.descripcion LIKE CONCAT('%', p_genero, '%') 
    ORDER BY p.titulo ASC;
END $$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;