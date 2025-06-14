package com.example.TransporteProductosLorica.Excepciones;
public class InformacionIncompletaExcepcion extends RuntimeException {
    public InformacionIncompletaExcepcion() {
        super("Información incompleta o inválida.");
    }

    public InformacionIncompletaExcepcion(String mensaje) {
        super(mensaje);
    }
}
