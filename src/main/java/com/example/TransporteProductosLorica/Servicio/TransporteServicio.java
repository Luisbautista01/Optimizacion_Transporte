package com.example.TransporteProductosLorica.Servicio;

import com.example.TransporteProductosLorica.Excepciones.InformacionIncompletaExcepcion;
import com.example.TransporteProductosLorica.Modelo.*;
import com.example.TransporteProductosLorica.Repositorio.TransporteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;

import java.util.List;

import static com.example.TransporteProductosLorica.Modelo.Producto.TipoCarga.*;

@Service
public class TransporteServicio {

    @Autowired
    private TransporteRepositorio transporteRepositorio;

    @Transactional
    public Transporte crearTransporte(Transporte t) {
        validarTransporte(t);
        calcularTiempoYCosto(t);
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

        validarTransporte(nuevo);

        existente.setVehiculo(nuevo.getVehiculo());
        existente.setProducto(nuevo.getProducto());
        existente.setRuta(nuevo.getRuta());
        existente.setCantidadKg(nuevo.getCantidadKg());

        calcularTiempoYCosto(existente);

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
            if (cantidadKg < v.getCapacidadKgMin() || cantidadKg > v.getCapacidadKgMax()) continue;

            double velocidadPromedio = (v.getVelocidadMin() + v.getVelocidadMax()) / 2.0;
            if (velocidadPromedio <= 0) continue;

            double tiempo = ruta.getDistanciaKm() / velocidadPromedio;
            double costo = calcularCosto(producto.getTipoCarga(), cantidadKg);

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

    public List<Vehiculo> obtenerVehiculosCompatibles(Producto producto, double cantidadKg, Ruta ruta, List<Vehiculo> vehiculos) {
        return vehiculos.stream()
                .filter(v -> cantidadKg >= v.getCapacidadKgMin() && cantidadKg <= v.getCapacidadKgMax())
                .filter(v -> {
                    if (producto.getTipoCarga() == Producto.TipoCarga.PERECEDERO || producto.getTipoCarga() == Producto.TipoCarga.FRUTA_FRESCA) {
                        String uso = v.getUsoComun().toLowerCase();
                        return uso.contains("refrigerado") || uso.contains("perecedera");
                    }
                    return true;
                })
                .toList();
    }

    public Transporte generarTransporte(Producto producto, double cantidadKg, Ruta ruta, Vehiculo vehiculo) {
        double velocidadPromedio = (vehiculo.getVelocidadMin() + vehiculo.getVelocidadMax()) / 2;
        double tiempoEstimadoHoras = ruta.getDistanciaKm() / velocidadPromedio;

        double tarifaBaseKm = 200;
        double multiplicador = (producto.getTipoCarga() == Producto.TipoCarga.PERECEDERO || producto.getTipoCarga() == Producto.TipoCarga.FRUTA_FRESCA) ? 1.5 : 1.0;
        double costoEstimado = ruta.getDistanciaKm() * tarifaBaseKm * multiplicador;

        Transporte t = new Transporte();
        t.setProducto(producto);
        t.setRuta(ruta);
        t.setVehiculo(vehiculo);
        t.setCantidadKg(cantidadKg);
        t.setTiempoEstimadoHoras(tiempoEstimadoHoras);
        t.setCostoEstimado(costoEstimado);

        t.setTiempoFormateado(formatearTiempo(tiempoEstimadoHoras));
        t.setCostoFormateado(formatearCosto(costoEstimado));

        return t;
    }

    private void calcularTiempoYCosto(Transporte t) {
        double velocidadPromedio = (t.getVehiculo().getVelocidadMin() + t.getVehiculo().getVelocidadMax()) / 2.0;
        if (velocidadPromedio <= 0) {
            throw new IllegalArgumentException("La velocidad promedio debe ser mayor que 0.");
        }

        double tiempo = t.getRuta().getDistanciaKm() / velocidadPromedio;
        t.setTiempoEstimadoHoras(Math.round(tiempo * 100.0) / 100.0);

        double costo = calcularCosto(t.getProducto().getTipoCarga(), t.getCantidadKg());
        t.setCostoEstimado(Math.round(costo * 100.0) / 100.0);
    }

    private double calcularCosto(Producto.TipoCarga tipoCarga, double cantidadKg) {
        double costo = cantidadKg * 80;

        switch (tipoCarga) {
            case REFRIGERADO -> costo *= 1.10;
            case PELIGROSO   -> costo *= 1.50;
            case AGRICOLA    -> costo *= 0.85;
            case LIVIANO     -> costo *= 0.95;
            case PERECEDERO, NO_PERECEDERO, GRANO, FRUTA_FRESCA -> costo *= 1.00;
            default -> throw new IllegalArgumentException("Tipo de carga no reconocido.");
        }

        if (cantidadKg > 1500) costo *= 0.85;
        else if (cantidadKg > 1000) costo *= 0.90;
        else if (cantidadKg > 500) costo *= 0.95;

        return costo;
    }

    private String formatearTiempo(double tiempoHoras) {
        int totalSegundos = (int) Math.round(tiempoHoras * 3600);
        int horas = totalSegundos / 3600;
        int minutos = (totalSegundos % 3600) / 60;
        int segundos = totalSegundos % 60;

        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }

    private String formatearCosto(double costo) {
        NumberFormat formatoPesos = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return formatoPesos.format(costo);
    }

    public boolean esCompatibleConTipoCarga(Producto producto, Vehiculo vehiculo) {
        // Aquí puedes establecer una lógica más compleja si lo deseas
        switch (producto.getTipoCarga()) {
            case NO_PERECEDERO:
                return vehiculo.getUsoComun().toLowerCase().contains("granel") || vehiculo.getCapacidadKgMax() >= 500;
            case PERECEDERO:
            case FRUTA_FRESCA:
                return vehiculo.getVelocidadMax() >= 40; // Para entregas rápidas
            case GRANO:
                return vehiculo.getUsoComun().toLowerCase().contains("granel") || vehiculo.getCapacidadKgMax() >= 1000;
            default:
                return true;
        }
    }

    public Vehiculo sugerirVehiculo(double cantidad, List<Vehiculo> compatibles) {
        return compatibles.stream()
                .filter(v -> v.getCapacidadKgMax() >= cantidad)
                .sorted(Comparator.comparingDouble(Vehiculo::getCapacidadKgMax))
                .findFirst()
                .orElse(compatibles.get(0));
    }

    private void validarTransporte(Transporte t) {
        if (t == null) throw new InformacionIncompletaExcepcion("El transporte no puede ser nulo.");
        if (t.getVehiculo() == null) throw new InformacionIncompletaExcepcion("Debe seleccionar un vehículo.");
        if (t.getRuta() == null) throw new InformacionIncompletaExcepcion("Debe seleccionar una ruta.");
        if (t.getProducto() == null) throw new InformacionIncompletaExcepcion("Debe seleccionar un producto.");
        if (t.getCantidadKg() <= 0) throw new InformacionIncompletaExcepcion("La cantidad en kg debe ser mayor que 0.");
        if (t.getCantidadKg() < t.getVehiculo().getCapacidadKgMin() || t.getCantidadKg() > t.getVehiculo().getCapacidadKgMax())
            throw new IllegalArgumentException("La cantidad en kg no está dentro del rango permitido del vehículo.");
        if (t.getRuta().getDistanciaKm() <= 0) throw new InformacionIncompletaExcepcion("La distancia de la ruta debe ser mayor que 0.");
        if (t.getVehiculo().getVelocidadMin() <= 0 || t.getVehiculo().getVelocidadMax() <= 0)
            throw new InformacionIncompletaExcepcion("Las velocidades del vehículo deben ser mayores que 0.");
    }
}

