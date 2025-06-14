package com.example.TransporteProductosLorica.Repositorio;

import com.example.TransporteProductosLorica.Modelo.Ruta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepositorio extends JpaRepository<Ruta,Long> {
    boolean existsByOrigenAndDestino(String origen, String destino);
    List<Ruta> findByOrigenContainingIgnoreCase(String origen);
    List<Ruta> findByDestinoContainingIgnoreCase(String destino);
    List<Ruta> findByHabilitada(boolean habilitada);

}
