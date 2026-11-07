-- ============================================
-- SCHEMA SQLite — Grabadores Externos de Video
-- Proyecto Integrador III - UNIAJC
-- ============================================

PRAGMA foreign_keys = ON;
PRAGMA journal_mode = WAL;

-- TABLA: Usuario
CREATE TABLE IF NOT EXISTS Usuario (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT    NOT NULL,
    correo      TEXT    NOT NULL UNIQUE,
    contrasena  TEXT    NOT NULL,
    rol         TEXT    NOT NULL CHECK(rol IN ('administrador', 'consultor')),
    activo      INTEGER NOT NULL DEFAULT 1
);

-- TABLA: GrabadorExterno
CREATE TABLE IF NOT EXISTS GrabadorExterno (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    marca           TEXT    NOT NULL,
    modelo          TEXT    NOT NULL,
    numeroSerie     TEXT    NOT NULL UNIQUE,
    anio            INTEGER,
    formatos        TEXT,
    resolucion      TEXT,
    conexiones      TEXT,
    almacenamiento  TEXT,
    precio          REAL    DEFAULT 0.0,
    imagenRuta      TEXT,
    activo          INTEGER NOT NULL DEFAULT 1
);

-- TABLA: Camara
CREATE TABLE IF NOT EXISTS Camara (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    marca           TEXT    NOT NULL,
    modelo          TEXT    NOT NULL,
    tipoConexion    TEXT,
    activo          INTEGER NOT NULL DEFAULT 1
);

-- TABLA: Reporte
CREATE TABLE IF NOT EXISTS Reporte (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha       TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    tipo        TEXT    NOT NULL,
    usuario_id  INTEGER NOT NULL,
    contenido   TEXT,
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);

-- TABLA: Inventario
CREATE TABLE IF NOT EXISTS Inventario (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    stock       INTEGER NOT NULL DEFAULT 0,
    estado      TEXT    NOT NULL CHECK(estado IN ('disponible','en_uso','mantenimiento','danado','baja')),
    grabador_id INTEGER NOT NULL,
    FOREIGN KEY (grabador_id) REFERENCES GrabadorExterno(id)
);

-- TABLA: Imágenes de referencia del grabador
CREATE TABLE IF NOT EXISTS Imagen (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    grabador_id     INTEGER NOT NULL,
    ruta            TEXT    NOT NULL,
    tipo_vista      TEXT    NOT NULL CHECK(tipo_vista IN ('FRONTAL','POSTERIOR','LATERAL')),
    UNIQUE(grabador_id, tipo_vista),
    FOREIGN KEY (grabador_id) REFERENCES GrabadorExterno(id)
);

-- TABLA: Historial auditable de movimientos de inventario
CREATE TABLE IF NOT EXISTS MovimientoInventario (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    grabador_id     INTEGER NOT NULL,
    usuario_id      INTEGER NOT NULL,
    tipo            TEXT    NOT NULL CHECK(tipo IN ('ENTRADA','SALIDA','AJUSTE','CAMBIO_ESTADO')),
    cantidad        INTEGER NOT NULL CHECK(cantidad >= 0),
    stock_anterior  INTEGER NOT NULL CHECK(stock_anterior >= 0),
    stock_nuevo     INTEGER NOT NULL CHECK(stock_nuevo >= 0),
    estado_anterior TEXT,
    estado_nuevo    TEXT,
    fecha           TEXT    NOT NULL DEFAULT (datetime('now','localtime')),
    observacion     TEXT,
    FOREIGN KEY (grabador_id) REFERENCES GrabadorExterno(id),
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
);

CREATE INDEX IF NOT EXISTS idx_movimiento_fecha ON MovimientoInventario(fecha);
CREATE INDEX IF NOT EXISTS idx_movimiento_grabador ON MovimientoInventario(grabador_id, fecha);

-- TABLA: Accesorio
CREATE TABLE IF NOT EXISTS Accesorio (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT    NOT NULL,
    descripcion TEXT,
    cantidad    INTEGER NOT NULL DEFAULT 0,
    estado      TEXT    NOT NULL DEFAULT 'disponible',
    grabador_id INTEGER NOT NULL,
    FOREIGN KEY (grabador_id) REFERENCES GrabadorExterno(id)
);

-- TABLA: Compatibilidad
CREATE TABLE IF NOT EXISTS Compatibilidad (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    tipoConexion    TEXT    NOT NULL,
    nivel           TEXT    NOT NULL CHECK(nivel IN ('total','parcial','no_compatible')),
    grabador_id     INTEGER NOT NULL,
    camara_id       INTEGER NOT NULL,
    FOREIGN KEY (grabador_id) REFERENCES GrabadorExterno(id),
    FOREIGN KEY (camara_id)   REFERENCES Camara(id),
    UNIQUE(grabador_id, camara_id)
);
