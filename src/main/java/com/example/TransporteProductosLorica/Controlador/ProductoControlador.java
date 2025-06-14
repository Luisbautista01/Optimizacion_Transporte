package com.example.TransporteProductosLorica.Controlador;

import com.example.TransporteProductosLorica.Modelo.Producto;
import com.example.TransporteProductosLorica.Servicio.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {

    @Autowired
    private ProductoServicio productoServicio;

    // Listar todos los productos
    @GetMapping
    public List<Producto> listarTodos() {
        return productoServicio.listarTodos();
    }

    // Crear un nuevo producto
    @PostMapping
    public Producto crear(@RequestBody Producto p) {
        return productoServicio.crearProducto(p);
    }

    // Actualizar un producto existente
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto p) {
        return productoServicio.actualizarProducto(id, p);
    }

    // Eliminar un producto por ID
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoServicio.eliminarProducto(id);
    }

    // Buscar productos por nombre (contiene, ignorando mayúsculas)
    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam("nombre") String nombre) {
        return productoServicio.buscarPorNombre(nombre);
    }

    // Filtrar productos por tipo de carga
    @GetMapping("/filtrar")
    public List<Producto> filtrarPorTipoCarga(@RequestParam("tipo") String tipoCarga) {
        return productoServicio.filtrarPorTipoCarga(tipoCarga);
    }
}
