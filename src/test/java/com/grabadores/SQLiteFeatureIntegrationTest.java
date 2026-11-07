package com.grabadores;

import com.grabadores.controller.CamaraController;
import com.grabadores.controller.CompatibilidadController;
import com.grabadores.controller.GrabadorController;
import com.grabadores.controller.ReporteController;
import com.grabadores.controller.UsuarioController;
import com.grabadores.dao.DatabaseHelper;
import com.grabadores.dao.InventarioDAO;
import com.grabadores.dao.MovimientoInventarioDAO;
import com.grabadores.model.Camara;
import com.grabadores.model.Compatibilidad;
import com.grabadores.model.GrabadorExterno;
import com.grabadores.model.Inventario;
import com.grabadores.model.MovimientoInventario;
import com.grabadores.model.Reporte;
import com.grabadores.model.Usuario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SQLiteFeatureIntegrationTest {

    @TempDir
    static Path tempDir;

    @BeforeAll
    static void initializeDatabase() {
        System.setProperty("grabadores.db.path", tempDir.resolve("features-test.db").toString());
        DatabaseHelper.getInstance().initializeDatabase();
    }

    @AfterAll
    static void closeDatabase() {
        DatabaseHelper.getInstance().closeConnection();
        System.clearProperty("grabadores.db.path");
    }

    @Test
    void registersInitialInventoryFiltersCompatibilityAndListsReportHistory() throws IOException {
        Usuario administrador = new UsuarioController().listarTodos().stream()
                .filter(Usuario::esAdministrador)
                .findFirst()
                .orElseThrow();

        GrabadorController grabadorController = new GrabadorController();
        GrabadorExterno grabador = crearGrabador("TEST-SERIE-001", "Grabador de prueba");
        assertEquals("OK", grabadorController.registrar(grabador, 7, "disponible", administrador.getId()));

        Inventario inventario = new InventarioDAO().buscarPorGrabadorId(grabador.getId());
        assertNotNull(inventario);
        assertEquals(7, inventario.getStock());
        assertEquals("disponible", inventario.getEstado());

        List<MovimientoInventario> movimientos = new MovimientoInventarioDAO().listarTodos();
        assertFalse(movimientos.stream().noneMatch(movimiento ->
                movimiento.getGrabadorId() == grabador.getId()
                        && "AJUSTE".equals(movimiento.getTipo())
                        && movimiento.getStockNuevo() == 7));

        GrabadorExterno duplicado = crearGrabador("TEST-SERIE-001", "Serie duplicada");
        assertFalse("OK".equals(grabadorController.registrar(
                duplicado, 3, "disponible", administrador.getId())));
        assertNull(new InventarioDAO().buscarPorGrabadorId(duplicado.getId()));
        assertEquals(1, grabadorController.listarTodos().stream()
                .filter(item -> "TEST-SERIE-001".equals(item.getNumeroSerie()))
                .count());

        Camara camara = new Camara("Marca de prueba", "Cámara de prueba", "HDMI");
        assertEquals("OK", new CamaraController().registrar(camara));
        CompatibilidadController compatibilidadController = new CompatibilidadController();
        assertEquals("OK", compatibilidadController.registrar(
                new Compatibilidad("HDMI", "total", grabador.getId(), camara.getId())));
        assertFalse(compatibilidadController.listarFiltradas(0, 0).stream()
                .noneMatch(item -> item.getGrabadorId() == grabador.getId()
                        && item.getCamaraId() == camara.getId()));
        assertEquals(1, compatibilidadController.listarFiltradas(grabador.getId(), 0).size());
        assertEquals(1, compatibilidadController.listarFiltradas(0, camara.getId()).size());
        assertEquals(1, compatibilidadController.listarFiltradas(
                grabador.getId(), camara.getId()).size());

        String contenidoEsperado = "Marca de prueba | Grabador de prueba";
        Path archivoPdf = tempDir.resolve("reporte-prueba.pdf");
        ReporteController reporteController = new ReporteController();
        assertTrue(reporteController.exportarTablaPDF(
                new String[]{"Marca", "Modelo"},
                List.<String[]>of(new String[]{"Marca de prueba", "Grabador de prueba"}),
                "Reporte de prueba", administrador.getNombre(), archivoPdf.toString(), administrador.getId()));
        assertTrue(Files.size(archivoPdf) > 0);
        Reporte listado = reporteController.listarHistorial().stream()
                .filter(item -> "Reporte de prueba".equals(item.getTipo()))
                .findFirst()
                .orElseThrow();
        assertTrue(listado.getContenido().contains(contenidoEsperado));
        assertNotNull(listado.getUsuario());
        assertEquals(administrador.getNombre(), listado.getUsuario().getNombre());
        assertTrue(reporteController.eliminarReporte(listado.getId()));
        assertFalse(reporteController.listarHistorial().stream()
                .anyMatch(item -> item.getId() == listado.getId()));
    }

    private GrabadorExterno crearGrabador(String serie, String modelo) {
        GrabadorExterno grabador = new GrabadorExterno();
        grabador.setMarca("Marca de prueba");
        grabador.setModelo(modelo);
        grabador.setNumeroSerie(serie);
        grabador.setAnio(2024);
        grabador.setFormatos("ProRes");
        grabador.setResolucion("4K");
        grabador.setConexiones("HDMI");
        grabador.setAlmacenamiento("SSD");
        grabador.setPrecio(100);
        return grabador;
    }
}
