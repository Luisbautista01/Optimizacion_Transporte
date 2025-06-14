package com.example.TransporteProductosLorica.Controlador;

import com.example.TransporteProductosLorica.Modelo.Vehiculo;
import com.example.TransporteProductosLorica.Servicio.VehiculoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import org.springframework.http.HttpHeaders;

import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoControlador {

    @Autowired
    private VehiculoServicio vehiculoServicio;

    // Listar todos los vehículos
    @GetMapping
    public List<Vehiculo> listar() {
        return vehiculoServicio.listarTodos();
    }

    // Crear vehículo sin imagen
    @PostMapping
    public Vehiculo crear(@RequestBody Vehiculo v) {
        return vehiculoServicio.crearVehiculo(v);
    }

    // Actualizar vehículo
    @PutMapping("/{id}")
    public Vehiculo actualizar(@PathVariable Long id, @RequestBody Vehiculo v) {
        return vehiculoServicio.actualizarVehiculo(id, v);
    }

    // Eliminar vehículo
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        vehiculoServicio.eliminarVehiculo(id);
    }

    @GetMapping("/imagen/{nombre}")
    public ResponseEntity<Resource> obtenerImagen(@PathVariable String nombre) throws MalformedURLException {
        Path rutaImagen = Paths.get("imagenes").resolve(nombre).normalize();
        Resource recurso = new UrlResource(rutaImagen.toUri());

        if (!recurso.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    // Buscar por tipo de vehículo (ej: "camión")
    @GetMapping("/buscar/tipo")
    public List<Vehiculo> buscarPorTipo(@RequestParam String tipo) {
        return vehiculoServicio.buscarPorTipo(tipo);
    }

    // Filtrar por uso común (ej: "productos agrícolas")
    @GetMapping("/filtrar/uso")
    public List<Vehiculo> filtrarPorUsoComun(@RequestParam String uso) {
        return vehiculoServicio.filtrarPorUsoComun(uso);
    }

    // Filtrar por capacidad mínima en kg (ej: >= 1000kg)
    @GetMapping("/filtrar/capacidad")
    public List<Vehiculo> filtrarPorCapacidad(@RequestParam double capacidadMin) {
        return vehiculoServicio.filtrarPorCapacidadKgMinima(capacidadMin);
    }
}
