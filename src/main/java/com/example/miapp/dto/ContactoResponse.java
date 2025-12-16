package com.example.miapp.dto;

import java.time.Instant;

public class ContactoResponse {
    private Long id;
    private String nombre;
    private String email;
    private String mensaje;
    private Instant fechaCreacion;

    // constructor de conveniencia
    public ContactoResponse(Long id, String nombre, String email, String mensaje, Instant fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.mensaje = mensaje;
        this.fechaCreacion = fechaCreacion;
    }

    // getters (setters si necesitas)
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getMensaje() { return mensaje; }
    public Instant getFechaCreacion() { return fechaCreacion; }
}