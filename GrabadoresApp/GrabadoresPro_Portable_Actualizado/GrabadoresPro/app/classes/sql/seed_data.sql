-- ============================================
-- DATOS DE PRUEBA — 10 registros por tabla
-- Proyecto Integrador III - UNIAJC
-- ============================================

-- Contraseñas hasheadas con SHA-256 (todas son "123456" para pruebas)
-- SHA-256 de "123456" = 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92

INSERT INTO Usuario (nombre, correo, contrasena, rol) VALUES
('Juan Sebastián Charrupi', 'juan.charrupi@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'administrador'),
('Alan Ledesma', 'alan.ledesma@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'administrador'),
('Victor Manuel', 'victor.manuel@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'administrador'),
('Carlos Méndez', 'carlos.mendez@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'consultor'),
('Laura García', 'laura.garcia@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'consultor'),
('Andrés Rodríguez', 'andres.rodriguez@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'consultor'),
('María Fernanda López', 'maria.lopez@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'consultor'),
('Santiago Pérez', 'santiago.perez@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'administrador'),
('Camila Torres', 'camila.torres@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'consultor'),
('Diego Ramírez', 'diego.ramirez@uniajc.edu.co', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'consultor');

INSERT INTO GrabadorExterno (marca, modelo, numeroSerie, anio, formatos, resolucion, conexiones, almacenamiento, precio) VALUES
('Atomos', 'Ninja V', 'ATM-NV-2024-001', 2023, 'Apple ProRes, Avid DNxHR', '4K UHD (3840x2160) 60p', 'HDMI 2.0', 'SSD 2.5" SATA', 499.00),
('Atomos', 'Shogun 7', 'ATM-SG7-2024-002', 2023, 'Apple ProRes, ProRes RAW, Avid DNxHR', '4K DCI (4096x2160) 60p', 'HDMI 2.0, 12G-SDI', 'SSD 2.5" SATA', 1299.00),
('Blackmagic Design', 'Video Assist 12G HDR', 'BMD-VA12G-2024-003', 2024, 'Blackmagic RAW, Apple ProRes', '4K DCI (4096x2160) 60p', '12G-SDI, HDMI 2.0', 'USB-C SSD', 895.00),
('Blackmagic Design', 'Video Assist 7" 3G', 'BMD-VA7-2024-004', 2023, 'Apple ProRes, Avid DNx', '1080p Full HD 60p', '3G-SDI, HDMI', 'SD Card UHS-II', 495.00),
('Sound Devices', 'PIX 270i', 'SD-P270I-2024-005', 2022, 'Apple ProRes, Avid DNxHD', '1080p Full HD 60p', '3G-SDI, HDMI 1.4', 'SSD 2.5" SATA', 2995.00),
('Atomos', 'Ninja V+', 'ATM-NVP-2024-006', 2023, 'Apple ProRes, ProRes RAW, H.265', '4K DCI (4096x2160) 60p, 8K RAW', 'HDMI 2.1', 'SSD 2.5" SATA NVMe', 799.00),
('Atomos', 'Sumo 19SE', 'ATM-S19-2024-007', 2022, 'Apple ProRes, Avid DNxHR', '4K DCI (4096x2160) 30p', 'HDMI 2.0, 12G-SDI x4', 'SSD 2.5" SATA', 1999.00),
('Blackmagic Design', 'HyperDeck Studio Mini', 'BMD-HDSM-2024-008', 2023, 'Apple ProRes, Avid DNx, H.264', '1080p Full HD 60p', '6G-SDI', 'SD Card, SSD USB-C', 645.00),
('Convergent Design', 'Odyssey 7Q+', 'CD-O7QP-2024-009', 2021, 'Apple ProRes, CDNG RAW', '4K UHD (3840x2160) 30p', 'HDMI 1.4, 3G-SDI', 'SSD 2.5" SATA', 1795.00),
('Video Devices', 'PIX-E5', 'VD-PE5-2024-010', 2022, 'Apple ProRes', '4K UHD (3840x2160) 30p', 'HDMI 2.0, 3G-SDI', 'SpeedDrive mSATA', 1445.00);

INSERT INTO Camara (marca, modelo, tipoConexion) VALUES
('Sony', 'A7S III', 'HDMI 2.0'),
('Sony', 'FX6', 'HDMI 2.0, 12G-SDI'),
('Canon', 'EOS C70', 'HDMI 2.0, 12G-SDI'),
('Canon', 'EOS R5', 'HDMI 2.0'),
('Blackmagic Design', 'Pocket Cinema Camera 6K Pro', 'HDMI 2.0'),
('Panasonic', 'Lumix GH6', 'HDMI 2.0'),
('Panasonic', 'EVA1', 'HDMI 2.0, 3G-SDI'),
('Nikon', 'Z9', 'HDMI 2.1'),
('RED', 'KOMODO 6K', 'HDMI 2.0, 12G-SDI'),
('Fujifilm', 'X-H2S', 'HDMI 2.0');

INSERT INTO Inventario (stock, estado, grabador_id) VALUES
(5, 'disponible', 1),
(3, 'disponible', 2),
(2, 'en_uso', 3),
(8, 'disponible', 4),
(1, 'mantenimiento', 5),
(4, 'disponible', 6),
(1, 'en_uso', 7),
(6, 'disponible', 8),
(0, 'baja', 9),
(2, 'danado', 10);

INSERT INTO Accesorio (nombre, descripcion, cantidad, estado, grabador_id) VALUES
('Batería NP-F970', 'Batería de ion-litio 6600mAh para Atomos Ninja V', 10, 'disponible', 1),
('SSD Samsung 870 EVO 1TB', 'Unidad de estado sólido SATA III para grabación', 5, 'disponible', 1),
('Cable HDMI 2.0 Atomos', 'Cable HDMI certificado 4K60 de 30cm', 8, 'disponible', 2),
('Master Caddy II', 'Caddy para SSD 2.5" compatible con Atomos', 6, 'disponible', 2),
('Batería LP-E6NH', 'Batería adaptadora para Blackmagic Video Assist', 4, 'disponible', 3),
('Protector de pantalla', 'Protector de vidrio templado para monitor 5"', 12, 'disponible', 4),
('Cable SDI BNC 12G', 'Cable SDI de alta velocidad 75 ohm, 1 metro', 3, 'disponible', 5),
('Funda protectora Atomos', 'Funda de transporte para Ninja V+ y accesorios', 2, 'disponible', 6),
('Soporte Sunhood', 'Parasol plegable para monitor de 19 pulgadas', 2, 'disponible', 7),
('SpeedDrive 480GB', 'SSD mSATA de alta velocidad para PIX-E5', 3, 'disponible', 10);

INSERT INTO Compatibilidad (tipoConexion, nivel, grabador_id, camara_id) VALUES
('HDMI 2.0', 'total', 1, 1),
('HDMI 2.0', 'total', 1, 4),
('HDMI 2.0', 'total', 1, 5),
('HDMI 2.0', 'total', 2, 2),
('12G-SDI', 'total', 2, 3),
('12G-SDI', 'total', 3, 2),
('HDMI 2.0', 'parcial', 3, 6),
('HDMI 2.0', 'total', 4, 10),
('3G-SDI', 'total', 5, 7),
('HDMI 2.1', 'total', 6, 8),
('HDMI 2.0', 'parcial', 6, 1),
('12G-SDI', 'total', 7, 9);

INSERT INTO Reporte (fecha, tipo, usuario_id, contenido) VALUES
('2026-01-15 10:30:00', 'inventario_general', 1, 'Reporte de inventario general - Enero 2026'),
('2026-01-20 14:00:00', 'por_formato', 2, 'Reporte de grabadores con formato ProRes RAW'),
('2026-02-05 09:15:00', 'compatibilidad', 1, 'Compatibilidad con Sony A7S III'),
('2026-02-18 11:45:00', 'inventario_general', 3, 'Reporte de inventario - Febrero 2026'),
('2026-03-01 08:00:00', 'por_formato', 1, 'Reporte de grabadores con formato BRAW'),
('2026-03-10 16:30:00', 'compatibilidad', 4, 'Compatibilidad con Canon C70'),
('2026-03-25 10:00:00', 'inventario_general', 2, 'Reporte de inventario - Marzo 2026'),
('2026-04-02 13:20:00', 'por_formato', 5, 'Reporte de grabadores con 4K DCI'),
('2026-04-15 15:00:00', 'compatibilidad', 1, 'Compatibilidad con RED KOMODO'),
('2026-05-01 09:00:00', 'inventario_general', 1, 'Reporte de inventario - Mayo 2026');
