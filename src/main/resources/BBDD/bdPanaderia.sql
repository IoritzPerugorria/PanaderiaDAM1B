CREATE DATABASE BDPANADERIA;
USE BDPANADERIA;

CREATE TABLE IF NOT EXISTS PRODUCTOS(
ID INT PRIMARY KEY AUTO_INCREMENT,
NOMBRE VARCHAR(50) NOT NULL UNIQUE,
PRECIO DOUBLE NOT NULL,
STOCK INT NOT NULL,
IMAGEN VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS INGREDIENTES(
ID INT PRIMARY KEY AUTO_INCREMENT,
NOMBRE VARCHAR(50) NOT NULL UNIQUE,
PRECIO DOUBLE NOT NULL,
STOCK INT NOT NULL,
IMAGEN VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS NECESITA(
PR_ID INT,
ING_ID INT,
CANTIDAD INT NOT NULL,
FOREIGN KEY (PR_ID) REFERENCES PRODUCTOS(ID),
FOREIGN KEY (ING_ID) REFERENCES INGREDIENTES(ID),
PRIMARY KEY (PR_ID, ING_ID)
);

CREATE TABLE IF NOT EXISTS USUARIO(
ID INT PRIMARY KEY AUTO_INCREMENT,
USUARIO VARCHAR(50) NOT NULL UNIQUE,
CONTRASENA VARCHAR(50) NOT NULL,
ROL ENUM("ADMIN", "LECTOR") NOT NULL,
CARTERA DOUBLE NOT NULL
);

INSERT INTO USUARIO (USUARIO, CONTRASENA, ROL, CARTERA) VALUES ("USUARIO1", "ABCD", "ADMIN", 100.0);
INSERT INTO USUARIO (USUARIO, CONTRASENA, ROL, CARTERA) VALUES ("USUARIO2", "ABCD", "LECTOR", 50.0);

INSERT INTO PRODUCTOS (NOMBRE, PRECIO, STOCK) VALUES ("Pan Baguette", 1.10, 10);
INSERT INTO PRODUCTOS (NOMBRE, PRECIO, STOCK) VALUES ("Pan Artesano", 2.50, 10);
INSERT INTO PRODUCTOS (NOMBRE, PRECIO, STOCK) VALUES ("Croissant", 1.20, 10);
INSERT INTO PRODUCTOS (NOMBRE, PRECIO, STOCK) VALUES ("Napolitana", 1.80, 10);
INSERT INTO PRODUCTOS (NOMBRE, PRECIO, STOCK) VALUES ("Bizcocho", 3.50, 10);
INSERT INTO PRODUCTOS (NOMBRE, PRECIO, STOCK) VALUES ("Trenza de Hojaldre", 12.00, 10);
INSERT INTO PRODUCTOS (NOMBRE, PRECIO, STOCK) VALUES ("Pastas", 0.40, 50);

INSERT INTO INGREDIENTES (NOMBRE, PRECIO, STOCK) VALUES ("Harina", 0.70, 30);
INSERT INTO INGREDIENTES (NOMBRE, PRECIO, STOCK) VALUES ("Levadura", 0.30, 30);
INSERT INTO INGREDIENTES (NOMBRE, PRECIO, STOCK) VALUES ("Azucar", 0.30, 30);
INSERT INTO INGREDIENTES (NOMBRE, PRECIO, STOCK) VALUES ("Chocolate", 0.80, 30);
INSERT INTO INGREDIENTES (NOMBRE, PRECIO, STOCK) VALUES ("Hojaldre", 0.60, 30);
INSERT INTO INGREDIENTES (NOMBRE, PRECIO, STOCK) VALUES ("Leche", 1.00, 30);
INSERT INTO INGREDIENTES (NOMBRE, PRECIO, STOCK) VALUES ("Huevos", 0.20, 30);



