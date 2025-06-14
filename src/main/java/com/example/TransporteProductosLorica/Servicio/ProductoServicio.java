package com.example.TransporteProductosLorica.Servicio;

import com.example.TransporteProductosLorica.Excepciones.InformacionIncompletaExcepcion;
import com.example.TransporteProductosLorica.Modelo.Producto;
import com.example.TransporteProductosLorica.Repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoServicio {
    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Transactional
    public Producto crearProducto(Producto producto) {
        validarProducto(producto);

        if (productoRepositorio.existsByNombre(producto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + producto.getNombre());
        }

        return productoRepositorio.save(producto);
    }
    
    @Transactional
    public Producto actualizarProducto(Long id, Producto nuevo) {
        validarProducto(nuevo);

        Producto existente = productoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));

        if (!existente.getNombre().equals(nuevo.getNombre()) &&
                productoRepositorio.existsByNombre(nuevo.getNombre())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + nuevo.getNombre());
        }

        existente.setNombre(nuevo.getNombre());
        existente.setTipoCarga(nuevo.getTipoCarga());
        existente.setCaracteristicas(nuevo.getCaracteristicas());

        return productoRepositorio.save(existente);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepositorio.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
        productoRepositorio.deleteById(id);
    }

    @Transactional
    public List<Producto> listarTodos() {
        return productoRepositorio.findAll();
    }

    @Transactional
    public List<Producto> buscarPorNombre(String nombre) {
    return productoRepositorio.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public List<Producto> filtrarPorTipoCarga(String tipoCarga) {
        try {
            Producto.TipoCarga tipoEnum = Producto.TipoCarga.valueOf(tipoCarga.toUpperCase());
            return productoRepositorio.findByTipoCarga(tipoEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de carga no válido: " + tipoCarga);
        }
    }

    public long contarProductos() {
        return productoRepositorio.count();
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    private void validarProducto(Producto producto) {
        if (producto == null) {
            throw new InformacionIncompletaExcepcion("El producto no puede ser nulo.");
        }

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new InformacionIncompletaExcepcion("El nombre del producto no puede estar vacío.");
        }

        if (producto.getTipoCarga() == null) {
            throw new InformacionIncompletaExcepcion("El tipo de carga no puede estar vacío.");
        }

        if (producto.getCaracteristicas() == null || producto.getCaracteristicas().isBlank()) {
            throw new InformacionIncompletaExcepcion("Las características no pueden estar vacías.");
        }
    }

}
