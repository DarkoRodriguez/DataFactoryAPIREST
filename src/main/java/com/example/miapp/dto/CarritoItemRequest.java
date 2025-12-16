package com.example.miapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarritoItemRequest {

    @JsonProperty("producto_id")
    @JsonAlias({"productoId"})
    private Long productoId;

    @JsonProperty("usuario_id")
    @JsonAlias({"usuarioId"})
    private Long usuarioId;

    @JsonProperty("session_id")
    @JsonAlias({"sessionId"})
    private String sessionId;

    private Integer cantidad;
    private String nombre;
    private String imagen;
    private Double precio;

    private IdWrapper producto;
    private IdWrapper usuario;

    public CarritoItemRequest() { }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Long getProductoId() {
        if (productoId != null) return productoId;
        if (producto != null) return producto.getId();
        return null;
    }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getUsuarioId() {
        if (usuarioId != null) return usuarioId;
        if (usuario != null) return usuario.getId();
        return null;
    }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public IdWrapper getProducto() { return producto; }
    public void setProducto(IdWrapper producto) { this.producto = producto; }

    public IdWrapper getUsuario() { return usuario; }
    public void setUsuario(IdWrapper usuario) { this.usuario = usuario; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IdWrapper {
        private Long id;
        public IdWrapper() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}