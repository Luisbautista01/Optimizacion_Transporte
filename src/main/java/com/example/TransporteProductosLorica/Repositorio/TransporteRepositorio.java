package com.example.TransporteProductosLorica.Repositorio;

import com.example.TransporteProductosLorica.Modelo.Transporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransporteRepositorio extends JpaRepository<Transporte, Long> {
}
