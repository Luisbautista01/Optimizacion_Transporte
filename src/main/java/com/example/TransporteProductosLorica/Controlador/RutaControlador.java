package com.example.TransporteProductosLorica.Controlador;

import com.example.TransporteProductosLorica.Modelo.Ruta;
import com.example.TransporteProductosLorica.Servicio.RutaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
public class RutaControlador {

    @Autowired
    private RutaServicio rutaServicio;

    // Listar todas las rutas
    @GetMapping
    public List<Ruta> listar() {
        return rutaServicio.listarTodas();
    }

    // Crear nueva ruta
    @PostMapping
    public Ruta crear(@RequestBody Ruta r) {
        return rutaServicio.crearRuta(r);
    }

    // Actualizar ruta existente
    @PutMapping("/{id}")
    public Ruta actualizar(@PathVariable Long id, @RequestBody Ruta r) {
        return rutaServicio.actualizarRuta(id, r);
    }

    // Eliminar ruta por ID
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rutaServicio.eliminarRuta(id);
    }

    // Buscar rutas por origen
    @GetMapping("/buscar/origen")
    public List<Ruta> buscarPorOrigen(@RequestParam("origen") String origen) {
        return rutaServicio.buscarPorOrigen(origen);
    }

    // Buscar rutas por destino
    @GetMapping("/buscar/destino")
    public List<Ruta> buscarPorDestino(@RequestParam("destino") String destino) {
        return rutaServicio.buscarPorDestino(destino);
    }

    // Filtrar rutas por si están habilitadas o no
    @GetMapping("/filtrar")
    public List<Ruta> filtrarHabilitadas(@RequestParam("habilitada") boolean habilitada) {
        return rutaServicio.filtrarHabilitadas(habilitada);
    }
}
