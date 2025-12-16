package com.example.miapp.dto;

public class CarritoItemResponse {
    private Long id;
    private Long productoId;
    private Long usuarioId;
    private String sessionId;
    private Integer cantidad;
    private String nombre;
    private Double precio;
    private String imagen;

    public CarritoItemResponse() {}

    public CarritoItemResponse(Long id, Long productoId, Long usuarioId, String sessionId,
                               Integer cantidad, String nombre, Double precio, String imagen) {
        this.id = id;
        this.productoId = productoId;
        this.usuarioId = usuarioId;
        this.sessionId = sessionId;
        this.cantidad = cantidad;
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
    }
    // getters (setters no estrictamente necesarios para response)
    public Long getId() { return id; }
    public Long getProductoId() { return productoId; }
    public Long getUsuarioId() { return usuarioId; }
    public String getSessionId() { return sessionId; }
    public Integer getCantidad() { return cantidad; }
    public String getNombre() { return nombre; }
    public Double getPrecio() { return precio; }
    public String getImagen() { return imagen; }
}