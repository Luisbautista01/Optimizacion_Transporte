package com.example.TransporteProductosLorica.Servicio;

import com.example.TransporteProductosLorica.Excepciones.InformacionIncompletaExcepcion;
import com.example.TransporteProductosLorica.Modelo.Vehiculo;
import com.example.TransporteProductosLorica.Repositorio.VehiculoRepositorio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehiculoServicio {
    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;

    @Transactional
    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        validarInformacionVehiculo(vehiculo);

        if (vehiculoRepositorio.existsByTipo(vehiculo.getTipo())) {
            throw new IllegalArgumentException("Ya existe un vehículo con tipo: " + vehiculo.getTipo());
        }

        return vehiculoRepositorio.save(vehiculo);
    }

    @Transactional
    public Vehiculo actualizarVehiculo(Long id, Vehiculo nuevoVehiculo) {
        Vehiculo existente = vehiculoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con ID: " + id));

        if (!existente.getTipo().equals(nuevoVehiculo.getTipo()) &&
                vehiculoRepositorio.existsByTipo(nuevoVehiculo.getTipo())) {
            throw new IllegalArgumentException("Ya existe un vehículo con tipo: " + nuevoVehiculo.getTipo());
        }

        existente.setTipo(nuevoVehiculo.getTipo());
        existente.setCapacidadKgMin(nuevoVehiculo.getCapacidadKgMin());
        existente.setCapacidadKgMax(nuevoVehiculo.getCapacidadKgMax());
        existente.setCapacidadM3Min(nuevoVehiculo.getCapacidadM3Min());
        existente.setCapacidadM3Max(nuevoVehiculo.getCapacidadM3Max());
        existente.setVelocidadMin(nuevoVehiculo.getVelocidadMin());
        existente.setVelocidadMax(nuevoVehiculo.getVelocidadMax());
        existente.setUsoComun(nuevoVehiculo.getUsoComun());

        return vehiculoRepositorio.save(existente);
    }

    @Transactional
    public void eliminarVehiculo(Long id) {
        if (!vehiculoRepositorio.existsById(id)) {
            throw new IllegalArgumentException("Vehículo no encontrado con ID: " + id);
        }
        vehiculoRepositorio.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listarTodos() {
        return vehiculoRepositorio.findAll();
    }

    @Transactional
    public List<Vehiculo> buscarPorTipo(String tipo) {
        return vehiculoRepositorio.findByTipoContainingIgnoreCase(tipo);
    }

    @Transactional
    public List<Vehiculo> filtrarPorUsoComun(String uso) {
        return vehiculoRepositorio.findByUsoComunContainingIgnoreCase(uso);
    }

    @Transactional
    public List<Vehiculo> filtrarPorCapacidadKgMinima(double capacidadMin) {
        return vehiculoRepositorio.findByCapacidadKgMinGreaterThanEqual(capacidadMin);
    }

    public long contarVehiculos() {
        return vehiculoRepositorio.count();
    }

    private void validarInformacionVehiculo(Vehiculo vehiculo) {
        if (vehiculo.getTipo() == null || vehiculo.getTipo().isBlank()) {
            throw new InformacionIncompletaExcepcion();
        }
    }
}

