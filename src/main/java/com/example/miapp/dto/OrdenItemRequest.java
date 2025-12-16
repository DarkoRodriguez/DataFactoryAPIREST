package com.example.miapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdenItemRequest {

    @JsonProperty("orden_id")
    @JsonAlias({"ordenId"})
    private Long ordenId;

    @JsonProperty("producto_id")
    @JsonAlias({"productoId"})
    private Long productoId;

    private String nombre;
    private Integer cantidad;
    
    @JsonProperty("precio_unitario")
    @JsonAlias({"precioUnitario"})
    private Double precioUnitario;

    private Double subtotal;

    public OrdenItemRequest() { }

    // Getters y setters
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