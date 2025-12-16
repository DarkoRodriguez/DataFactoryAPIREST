package com.example.miapp.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Esta clase se convierte en tabla
@Data // Lombok genera getters/setters/toString/etc.
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor 
@Table(name="orden")

public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "numero_orden", unique = true, nullable = false)
    private String numeroOrden;

    
    private String estado = "PENDIENTE";

    @Column(name = "total")
    private Double total;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    private String apellidos;
    private String correo;
    private String calle;
    private String departamento;
    private String region;
    private String comuna;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<OrdenItem> items;

    
}

