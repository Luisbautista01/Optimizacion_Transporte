package com.example.TransporteProductosLorica.Modelo;

import jakarta.persistence.*;

@Entity
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String tipo;
    private double capacidadKgMin;
    private double capacidadKgMax;
    private double capacidadM3Min;
    private double capacidadM3Max;
    private double velocidadMin;
    private double velocidadMax;
    private String usoComun;

    public Vehiculo(String tipo, double capacidadKgMin, double capacidadKgMax, double capacidadM3Min,
                    double capacidadM3Max, double velocidadMin, double velocidadMax, String usoComun) {
        this.tipo = tipo;
        this.capacidadKgMin = capacidadKgMin;
        this.capacidadKgMax = capacidadKgMax;
        this.capacidadM3Min = capacidadM3Min;
        this.capacidadM3Max = capacidadM3Max;
        this.velocidadMin = velocidadMin;
        this.velocidadMax = velocidadMax;
        this.usoComun = usoComun;
    }

    public Vehiculo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCapacidadKgMin() {
        return capacidadKgMin;
    }

    public void setCapacidadKgMin(double capacidadKgMin) {
        this.capacidadKgMin = capacidadKgMin;
    }

    public double getCapacidadKgMax() {
        return capacidadKgMax;
    }

    public void setCapacidadKgMax(double capacidadKgMax) {
        this.capacidadKgMax = capacidadKgMax;
    }

    public double getCapacidadM3Min() {
        return capacidadM3Min;
    }

    public void setCapacidadM3Min(double capacidadM3Min) {
        this.capacidadM3Min = capacidadM3Min;
    }

    public double getCapacidadM3Max() {
        return capacidadM3Max;
    }

    public void setCapacidadM3Max(double capacidadM3Max) {
        this.capacidadM3Max = capacidadM3Max;
    }

    public double getVelocidadMin() {
        return velocidadMin;
    }

    public void setVelocidadMin(double velocidadMin) {
        this.velocidadMin = velocidadMin;
    }

    public double getVelocidadMax() {
        return velocidadMax;
    }

    public void setVelocidadMax(double velocidadMax) {
        this.velocidadMax = velocidadMax;
    }

    public String getUsoComun() {
        return usoComun;
    }

    public void setUsoComun(String usoComun) {
        this.usoComun = usoComun;
    }
}