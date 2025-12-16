package com.example.miapp.dto;

public class OrdenItemResponse {
    private Long id;
    private Long ordenId;
    private Long productoId;
    private String nombre;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public OrdenItemResponse() { }

    public OrdenItemResponse(Long id, Long ordenId, Long productoId, String nombre,
                             Integer cantidad, Double precioUnitario, Double subtotal) {
        this.id = id;
        this.ordenId = ordenId;
        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}