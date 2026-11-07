# Proyecto-integrador 

---Prototipo de Aplicacion de eEscritorio Para la gestión de Grabadores Externos de Video Profesionales


# Integrantes;

Juan Sebastian Charrupi
Brayan Andres Garcia
Lior Alberto Zuluaga


# Requisitos
- **Para usar la versión portable:** Windows. No es necesario instalar Java.
- **Para ejecutar desde el código fuente:** JDK 17 o superior y Maven.

## Abrir la aplicación portable

1. Abra la carpeta `GrabadoresPro_Portable_Actualizado`.
2. Entre en `GrabadoresPro`.
3. Ejecute `GrabadoresPro.exe`.
4. En el inicio de sesión, use una cuenta de prueba:

| Administrador | `profesor.123@uniajc.edu.co` | `123456` |
   | Consultor | `carlos.mendez@uniajc.edu.co` | `123456` | ----este es solo un ejemplo xd


La primera ejecución crea automáticamente la base de datos SQLite y carga datos de prueba. Para conservar la base de datos, no elimine el archivo `grabadores.db` creado por la aplicación.

## Uso básico

Después de iniciar sesión, use el menú lateral:

- **Grabadores:** consultar y administrar grabadores registrados.
- **Cámaras:** consultar y administrar cámaras.
- **Inventario:** revisar existencias y registrar movimientos.
- **Accesorios:** administrar accesorios asociados a los equipos.
- **Compatibilidad:** consultar y gestionar compatibilidades entre grabadores y cámaras.
- **Comparador:** comparar las características de los grabadores.
- **Generar Reportes:** seleccionar un tipo de reporte, aplicar sus filtros, generar los resultados y exportarlos a PDF. En **Historial de reportes** se pueden consultar los reportes exportados y eliminar un registro del historial. Eliminarlo del historial no borra el archivo PDF guardado en el equipo.
- **Gestión de Usuarios:** administrar usuarios (solo administradores).
