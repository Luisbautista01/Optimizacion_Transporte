package com.example.TransporteProductosLorica.Controlador;

import com.example.TransporteProductosLorica.Modelo.Transporte;
import com.example.TransporteProductosLorica.Servicio.TransporteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportes")
public class TransporteControlador {

    @Autowired
    private TransporteServicio transporteServicio;

    // Listar todos los transportes
    @GetMapping
    public List<Transporte> listar() {
        return transporteServicio.listarTodos();
    }

    // Obtener un transporte por su ID
    @GetMapping("/{id}")
    public Transporte obtenerPorId(@PathVariable Long id) {
        return transporteServicio.obtenerTransportePorId(id);
    }

    // Crear un nuevo transporte
    @PostMapping
    public Transporte crear(@RequestBody Transporte t) {
        return transporteServicio.crearTransporte(t);
    }

    // Eliminar transporte por ID
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        transporteServicio.eliminarTransporte(id);
    }

    @PutMapping("/{id}")
    public Transporte actualizar(@PathVariable Long id, @RequestBody Transporte t) {
        return transporteServicio.actualizarTransporte(id, t);
    }
}
