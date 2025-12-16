package com.example.miapp.dto;

import java.util.List;

public class OrdenResponse {
    private Long id;
    private Long usuarioId;
    private String numeroOrden;
    private String estado;
    private Double total;
    private String nombreCompleto;
    private String apellidos;
    private String correo;
    private String calle;
    private String departamento;
    private String region;
    private String comuna;
    private String indicaciones;
    private List<OrdenItemResponse> items;

    public OrdenResponse() { }

    public OrdenResponse(Long id, Long usuarioId, String numeroOrden, String estado, Double total,
                         String nombreCompleto, String apellidos, String correo, String calle,
                         String departamento, String region, String comuna, String indicaciones) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.numeroOrden = numeroOrden;
        this.estado = estado;
        this.total = total;
        this.nombreCompleto = nombreCompleto;
        this.apellidos = apellidos;
        this.correo = correo;
        this.calle = calle;
        this.departamento = departamento;
        this.region = region;
        this.comuna = comuna;
        this.indicaciones = indicaciones;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }

    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }

    public List<OrdenItemResponse> getItems() { return items; }
    public void setItems(List<OrdenItemResponse> items) { this.items = items; }

    // DTO simple para OrdenItem (si lo necesitas)
    public static class OrdenItemResponse {
        private Long id;
        private Long productoId;
        private String nombre;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;

        public OrdenItemResponse() { }
        public OrdenItemResponse(Long id, Long productoId, String nombre, Integer cantidad, Double precioUnitario, Double subtotal) {
            this.id = id;
            this.productoId = productoId;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = subtotal;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
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
}