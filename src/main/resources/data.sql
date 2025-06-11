INSERT INTO Categorias (nombre, activo) VALUES ('Guitarras', true);
INSERT INTO Categorias (nombre, activo) VALUES ('Teclados', true);
INSERT INTO Categorias (nombre, activo) VALUES ('Percusión', true);
INSERT INTO Categorias (nombre, activo) VALUES ('Accesorios', false);
INSERT INTO Categorias (nombre, activo) VALUES ('Eléctricas', true);

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

-- Producto de prueba para update
INSERT INTO Productos (nombre, comentarios, foto_url, marca_id, stock, precio, activo)
VALUES ('Guitarra Les Paul', 'Guitarra clásica', 'https://ejemplo.com/lespaul.jpg', 2, 10, 1500.00, true);

-- Productos adicionales para testing
INSERT INTO Productos (nombre, comentarios, foto_url, marca_id, stock, precio, activo)
VALUES ('Guitarra RG Series', 'Guitarra eléctrica para rock', 'https://ejemplo.com/ibanez.jpg', 4, 8, 1200.00, false);

INSERT INTO Productos (nombre, comentarios, foto_url, marca_id, stock, precio, activo)
VALUES ('Teclado PSR-E373', 'Teclado digital 61 teclas', 'https://ejemplo.com/yamaha-teclado.jpg', 3, 15, 800.00, true);

INSERT INTO Productos (nombre, comentarios, foto_url, marca_id, stock, precio, activo)
VALUES ('Micrófono SM58', 'Micrófono dinámico profesional', 'https://ejemplo.com/mic.jpg', 3, 25, 150.00, true);

INSERT INTO Productos (nombre, comentarios, foto_url, marca_id, stock, precio, activo)
VALUES ('Batería Electrónica DTX432K', 'Kit de batería electrónica', 'https://ejemplo.com/yamaha-drums.jpg', 3, 5, 2500.00, true);

INSERT INTO Productos (nombre, comentarios, foto_url, marca_id, stock, precio, activo)
VALUES ('Guitarra Stratocaster', 'Guitarra eléctrica clásica', 'https://ejemplo.com/fender-strat.jpg', 1, 12, 1800.00, true);


-- Relaciones iniciales del producto (ID 1 suponiendo que es el primer producto)
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (1, 1, true);  -- Guitarras
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (1, 3, true);  -- Percusión

-- Relaciones para los productos (IDs 2-6)
-- Guitarra Ibanez (ID 2): Guitarras + Eléctricas
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (2, 1, true);  -- Guitarras

INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (2, 5, true);  -- Eléctricas

-- Teclado Yamaha (ID 3): Teclados
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (3, 2, true);  -- Teclados

-- Micrófono Yamaha (ID 4): Accesorios (reactivar la categoría)
UPDATE Categorias SET activo = true WHERE id = 4;  -- Reactivar Accesorios
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (4, 4, true);  -- Accesorios

-- Batería Yamaha (ID 5): Percusión + Eléctricas
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (5, 3, true);  -- Percusión
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (5, 5, true);  -- Eléctricas

-- Guitarra Fender (ID 6): Guitarras + Eléctricas
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (6, 1, true);  -- Guitarras
INSERT INTO Productos_X_Categorias (producto_id, categoria_id, activo) VALUES (6, 5, true);  -- Eléctricas