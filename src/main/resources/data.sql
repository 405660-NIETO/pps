INSERT INTO Categorias (nombre, activo) VALUES ('Guitarras', true);
INSERT INTO Categorias (nombre, activo) VALUES ('Teclados', true);
INSERT INTO Categorias (nombre, activo) VALUES ('Percusión', true);
INSERT INTO Categorias (nombre, activo) VALUES ('Accesorios', false);

INSERT INTO Marcas (nombre, activo) VALUES ('Fender', true);
INSERT INTO Marcas (nombre, activo) VALUES ('Gibson', true);
INSERT INTO Marcas (nombre, activo) VALUES ('Yamaha', true);
INSERT INTO Marcas (nombre, activo) VALUES ('Ibañez', true);

INSERT INTO Trabajos (nombre, activo) VALUES ('Cambio de Cuerdas', true);
INSERT INTO Trabajos (nombre, activo) VALUES ('Calibracion', true);
INSERT INTO Trabajos (nombre, activo) VALUES ('Cambio de Traste', false);
INSERT INTO Trabajos (nombre, activo) VALUES ('Cambio de Pastillas', true);

INSERT INTO Forma_Pago (nombre, activo) VALUES ('Efectivo', true);
INSERT INTO Forma_Pago (nombre, activo) VALUES ('Debito', true);
INSERT INTO Forma_Pago (nombre, activo) VALUES ('Credito', true);
INSERT INTO Forma_Pago (nombre, activo) VALUES ('Prepago', true);
INSERT INTO Forma_Pago (nombre, activo) VALUES ('MODO', true);
INSERT INTO Forma_Pago (nombre, activo) VALUES ('Transferencia', true);

INSERT INTO Sedes (nombre, direccion, activo) VALUES ('Tienda de Musica', 'Calle 1', true);
INSERT INTO Sedes (nombre, direccion, activo) VALUES ('Tienda de Musica', 'Calle 2', false);
INSERT INTO Sedes (nombre, direccion, activo) VALUES ('Tienda de Musica', 'Calle 3', true);
INSERT INTO Sedes (nombre, direccion, activo) VALUES ('Tienda de Musica', 'Calle 4', true);

INSERT INTO Roles (nombre, activo) VALUES ('ADMINISTRADOR', true);
INSERT INTO Roles (nombre, activo) VALUES ('VENDEDOR', false);
INSERT INTO Roles (nombre, activo) VALUES ('EMPLEADO', true);
INSERT INTO Roles (nombre, activo) VALUES ('LUTHIER', true);