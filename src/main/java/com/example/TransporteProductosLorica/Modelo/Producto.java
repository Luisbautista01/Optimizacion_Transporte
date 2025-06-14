package com.example.TransporteProductosLorica.Modelo;

import jakarta.persistence.*;

@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoCarga tipoCarga;
    private String caracteristicas;

    public Producto(String nombre, TipoCarga tipoCarga, String caracteristicas) {
        this.nombre = nombre;
        this.tipoCarga = tipoCarga;
        this.caracteristicas = caracteristicas;
    }

    public Producto() {
    }

    public enum TipoCarga {
        REFRIGERADO,
        PELIGROSO,
        AGRICOLA,
        LIVIANO,
        PERECEDERO,
        NO_PERECEDERO,
        GRANO,
        FRUTA_FRESCA
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoCarga getTipoCarga() {
        return tipoCarga;
    }

    public void setTipoCarga(TipoCarga tipoCarga) {
        this.tipoCarga = tipoCarga;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }
}