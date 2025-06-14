package com.example.TransporteProductosLorica.Repositorio;

import com.example.TransporteProductosLorica.Modelo.Producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
    boolean existsByNombre(String nombre);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByTipoCargaIgnoreCase(String tipoCarga);
}
