package com.example.TransporteProductosLorica.Modelo;

import jakarta.persistence.*;

@Entity
public class Transporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vehiculo vehiculo;

    @ManyToOne
    private Producto producto;

    @ManyToOne
    private Ruta ruta;

    private double cantidadKg;
    private double tiempoEstimadoHoras;
    private double costoEstimado;

    public Transporte(Long id, Vehiculo vehiculo, Producto producto, Ruta ruta,
                      double cantidadKg, double tiempoEstimadoHoras, double costoEstimado) {
        this.id = id;
        this.vehiculo = vehiculo;
        this.producto = producto;
        this.ruta = ruta;
        this.cantidadKg = cantidadKg;
        this.tiempoEstimadoHoras = tiempoEstimadoHoras;
        this.costoEstimado = costoEstimado;
    }

    public Transporte() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public double getCantidadKg() {
        return cantidadKg;
    }

    public void setCantidadKg(double cantidadKg) {
        this.cantidadKg = cantidadKg;
    }

    public double getTiempoEstimadoHoras() {
        return tiempoEstimadoHoras;
    }

    public void setTiempoEstimadoHoras(double tiempoEstimadoHoras) {
        this.tiempoEstimadoHoras = tiempoEstimadoHoras;
    }

    public double getCostoEstimado() {
        return costoEstimado;
    }

    public void setCostoEstimado(double costoEstimado) {
        this.costoEstimado = costoEstimado;
    }

}
