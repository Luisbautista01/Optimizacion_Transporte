package com.example.TransporteProductosLorica.Servicio;

import com.example.TransporteProductosLorica.Excepciones.InformacionIncompletaExcepcion;
import com.example.TransporteProductosLorica.Modelo.Producto;
import com.example.TransporteProductosLorica.Modelo.Ruta;
import com.example.TransporteProductosLorica.Modelo.Transporte;
import com.example.TransporteProductosLorica.Modelo.Vehiculo;
import com.example.TransporteProductosLorica.Repositorio.TransporteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransporteServicio {

    @Autowired
    private TransporteRepositorio transporteRepositorio;

    @Transactional
    public Transporte crearTransporte(Transporte t) {
        validarTransporte(t);

        // Calcula la velocidad promedio
        double velocidadMin = t.getVehiculo().getVelocidadMin();
        double velocidadMax = t.getVehiculo().getVelocidadMax();
        double velocidadPromedio = (velocidadMin + velocidadMax) / 2.0;

        if (velocidadPromedio <= 0) {
            throw new IllegalArgumentException("La velocidad promedio debe ser mayor que 0.");
        }

        // Calcula el tiempo estimado
        double tiempo = t.getRuta().getDistanciaKm() / velocidadPromedio;
        t.setTiempoEstimadoHoras(Math.round(tiempo * 100.0) / 100.0);

        // Costo base por kilo en Lorica
        double costo = t.getCantidadKg() * 80; // ↓ Ajustado a contexto local

        // Ajuste por tipo de carga (Lorica, Córdoba)
        String tipo = t.getProducto().getTipoCarga().toLowerCase();
        switch (tipo) {
            case "refrigerado" -> costo *= 1.10;
            case "peligroso" -> costo *= 1.50;
            case "agrícola" -> costo *= 0.85;
            case "liviano" -> costo *= 0.95;
            default -> throw new IllegalArgumentException("Tipo de carga no reconocido: " + tipo);
        }

        // Descuentos por cantidad (según contexto local)
        if (t.getCantidadKg() > 1500) {
            costo *= 0.85;
        } else if (t.getCantidadKg() > 1000) {
            costo *= 0.90;
        } else if (t.getCantidadKg() > 500) {
            costo *= 0.95;
        }

        // Redondear costo final
        t.setCostoEstimado(Math.round(costo * 100.0) / 100.0);

        return transporteRepositorio.save(t);
    }

    @Transactional
    public Transporte guardarTransporte(Transporte transporte) {
        validarTransporte(transporte);
        return transporteRepositorio.save(transporte);
    }

    @Transactional
    public void eliminarTransporte(Long id) {
        if (!transporteRepositorio.existsById(id)) {
            throw new IllegalArgumentException("No existe transporte con ID: " + id);
        }
        transporteRepositorio.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Transporte> listarTodos() {
        return transporteRepositorio.findAll();
    }

    @Transactional
    public Transporte actualizarTransporte(Long id, Transporte nuevo) {
        Transporte existente = transporteRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe transporte con ID: " + id));

        // Validación
        validarTransporte(nuevo);

        // Reasignar relaciones
        existente.setVehiculo(nuevo.getVehiculo());
        existente.setProducto(nuevo.getProducto());
        existente.setRuta(nuevo.getRuta());
        existente.setCantidadKg(nuevo.getCantidadKg());

        // Calcular velocidad promedio
        double velocidadMin = nuevo.getVehiculo().getVelocidadMin();
        double velocidadMax = nuevo.getVehiculo().getVelocidadMax();
        double velocidadPromedio = (velocidadMin + velocidadMax) / 2.0;

        if (velocidadPromedio <= 0) {
            throw new IllegalArgumentException("La velocidad promedio debe ser mayor que 0.");
        }

        // Calcular tiempo estimado
        double tiempo = nuevo.getRuta().getDistanciaKm() / velocidadPromedio;
        existente.setTiempoEstimadoHoras(Math.round(tiempo * 100.0) / 100.0);

        // Calcular costo
        double costo = nuevo.getCantidadKg() * 80;

        String tipo = nuevo.getProducto().getTipoCarga().toLowerCase();
        switch (tipo) {
            case "refrigerado" -> costo *= 1.10;
            case "peligroso" -> costo *= 1.50;
            case "agrícola" -> costo *= 0.85;
            case "liviano" -> costo *= 0.95;
            default -> throw new IllegalArgumentException("Tipo de carga no reconocido: " + tipo);
        }

        if (nuevo.getCantidadKg() > 1500) {
            costo *= 0.85;
        } else if (nuevo.getCantidadKg() > 1000) {
            costo *= 0.90;
        } else if (nuevo.getCantidadKg() > 500) {
            costo *= 0.95;
        }

        existente.setCostoEstimado(Math.round(costo * 100.0) / 100.0);

        return transporteRepositorio.save(existente);
    }

    @Transactional(readOnly = true)
    public Transporte obtenerTransportePorId(Long id) {
        return transporteRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe transporte con ID: " + id));
    }

    public Transporte sugerirTransporteOptimizado(Producto producto, double cantidadKg, Ruta ruta, List<Vehiculo> vehiculos) {
        if (vehiculos == null || vehiculos.isEmpty()) {
            throw new IllegalArgumentException("No hay vehículos disponibles.");
        }

        Vehiculo mejorVehiculo = null;
        double mejorCosto = Double.MAX_VALUE;
        double mejorTiempo = 0.0;

        for (Vehiculo v : vehiculos) {
            if (cantidadKg < v.getCapacidadKgMin() || cantidadKg > v.getCapacidadKgMax()) {
                continue;
            }

            double velocidadPromedio = (v.getVelocidadMin() + v.getVelocidadMax()) / 2.0;
            if (velocidadPromedio <= 0) continue;

            double tiempo = ruta.getDistanciaKm() / velocidadPromedio;
            double costo = cantidadKg * 80;

            switch (producto.getTipoCarga().toLowerCase()) {
                case "refrigerado" -> costo *= 1.10;
                case "peligroso" -> costo *= 1.50;
                case "agrícola" -> costo *= 0.85;
                case "liviano" -> costo *= 0.95;
            }

            if (cantidadKg > 1500) costo *= 0.85;
            else if (cantidadKg > 1000) costo *= 0.90;
            else if (cantidadKg > 500) costo *= 0.95;

            if (costo < mejorCosto) {
                mejorCosto = costo;
                mejorVehiculo = v;
                mejorTiempo = tiempo;
            }
        }

        if (mejorVehiculo == null) {
            throw new IllegalArgumentException("No se encontró un vehículo adecuado para esa carga.");
        }

        Transporte transporte = new Transporte();
        transporte.setVehiculo(mejorVehiculo);
        transporte.setProducto(producto);
        transporte.setRuta(ruta);
        transporte.setCantidadKg(cantidadKg);
        transporte.setTiempoEstimadoHoras(Math.round(mejorTiempo * 100.0) / 100.0);
        transporte.setCostoEstimado(Math.round(mejorCosto * 100.0) / 100.0);

        return transporte;
    }


    private void validarTransporte(Transporte t) {
        if (t == null) {
            throw new InformacionIncompletaExcepcion("El transporte no puede ser nulo.");
        }

        if (t.getVehiculo() == null) {
            throw new InformacionIncompletaExcepcion("Debe seleccionar un vehículo.");
        }

        if (t.getRuta() == null) {
            throw new InformacionIncompletaExcepcion("Debe seleccionar una ruta.");
        }

        if (t.getProducto() == null) {
            throw new InformacionIncompletaExcepcion("Debe seleccionar un producto.");
        }

        if (t.getCantidadKg() <= 0) {
            throw new InformacionIncompletaExcepcion("La cantidad en kg debe ser mayor que 0.");
        }

        if (t.getCantidadKg() < t.getVehiculo().getCapacidadKgMin() ||
                t.getCantidadKg() > t.getVehiculo().getCapacidadKgMax()) {
            throw new IllegalArgumentException("La cantidad en kg no está dentro del rango permitido del vehículo.");
        }

        if (t.getRuta().getDistanciaKm() <= 0) {
            throw new InformacionIncompletaExcepcion("La distancia de la ruta debe ser mayor que 0.");
        }

        if (t.getVehiculo().getVelocidadMin() <= 0 || t.getVehiculo().getVelocidadMax() <= 0) {
            throw new InformacionIncompletaExcepcion("Las velocidades del vehículo deben ser mayores que 0.");
        }
    }
}
