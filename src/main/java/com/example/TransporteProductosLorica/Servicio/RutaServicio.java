package com.example.TransporteProductosLorica.Servicio;

import com.example.TransporteProductosLorica.Modelo.Ruta;
import com.example.TransporteProductosLorica.Repositorio.RutaRepositorio;
import com.example.TransporteProductosLorica.Excepciones.InformacionIncompletaExcepcion;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RutaServicio {

    @Autowired
    private RutaRepositorio rutaRepositorio;

    @Transactional
    public Ruta crearRuta(Ruta ruta) {
        validarRuta(ruta);

        if (rutaRepositorio.existsByOrigenAndDestino(ruta.getOrigen(), ruta.getDestino())) {
            throw new IllegalArgumentException("Ya existe una ruta de " + ruta.getOrigen() + " a " + ruta.getDestino());
        }

        return rutaRepositorio.save(ruta);
    }

    @Transactional
    public Ruta actualizarRuta(Long id, Ruta nueva) {
        validarRuta(nueva);

        Ruta existente = rutaRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + id));

        boolean cambiaronOrigenDestino = !existente.getOrigen().equals(nueva.getOrigen()) ||
                                         !existente.getDestino().equals(nueva.getDestino());

        if (cambiaronOrigenDestino &&
                rutaRepositorio.existsByOrigenAndDestino(nueva.getOrigen(), nueva.getDestino())) {
            throw new IllegalArgumentException("Ya existe una ruta de " + nueva.getOrigen() + " a " + nueva.getDestino());
        }

        existente.setOrigen(nueva.getOrigen());
        existente.setDestino(nueva.getDestino());
        existente.setDistanciaKm(nueva.getDistanciaKm());
        existente.setHabilitada(nueva.isHabilitada());

        return rutaRepositorio.save(existente);
    }

    @Transactional
    public void eliminarRuta(Long id) {
        if (!rutaRepositorio.existsById(id)) {
            throw new IllegalArgumentException("Ruta no encontrada con ID: " + id);
        }
        rutaRepositorio.deleteById(id);
    }

    @Transactional
    public List<Ruta> listarTodas() {
        return rutaRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Ruta> listarHabilitadas() {
        return rutaRepositorio.findAll().stream()
                .filter(Ruta::isHabilitada)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Ruta> filtrarHabilitadas(boolean habilitada) {
        return rutaRepositorio.findByHabilitada(habilitada);
    }

    @Transactional
    public List<Ruta> buscarPorOrigen(String origen) {
        return rutaRepositorio.findByOrigenContainingIgnoreCase(origen);
    }

    @Transactional
    public List<Ruta> buscarPorDestino(String destino) {
        return rutaRepositorio.findByDestinoContainingIgnoreCase(destino);
    }

    public long contarRutas() {
        return rutaRepositorio.count();
    }

    @Transactional(readOnly = true)
    public Ruta buscarPorId(Long id) {
        return rutaRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + id));
    }


    private void validarRuta(Ruta ruta) {
        if (ruta == null) {
            throw new InformacionIncompletaExcepcion("La ruta no puede ser nula.");
        }

        if (ruta.getOrigen().trim().isEmpty()) {
            throw new InformacionIncompletaExcepcion("El origen no puede estar vacío.");
        }

        if (ruta.getDestino() == null || ruta.getDestino().isBlank()) {
            throw new InformacionIncompletaExcepcion("El destino no puede estar vacío.");
        }

        if (ruta.getOrigen().equalsIgnoreCase(ruta.getDestino())) {
            throw new InformacionIncompletaExcepcion("El origen y destino no pueden ser iguales.");
        }

        if (ruta.getDistanciaKm() <= 0) {
            throw new InformacionIncompletaExcepcion("La distancia debe ser mayor que 0.");
        }
    }
}
