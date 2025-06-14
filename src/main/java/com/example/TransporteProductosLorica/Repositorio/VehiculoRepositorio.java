package com.example.TransporteProductosLorica.Repositorio;

import com.example.TransporteProductosLorica.Modelo.Vehiculo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepositorio extends JpaRepository<Vehiculo, Long> {
    boolean existsByTipo(String tipo);
    List<Vehiculo> findByTipoContainingIgnoreCase(String tipo);
    List<Vehiculo> findByUsoComunContainingIgnoreCase(String usoComun);
    List<Vehiculo> findByCapacidadKgMinGreaterThanEqual(double capacidadKgMin);
}
