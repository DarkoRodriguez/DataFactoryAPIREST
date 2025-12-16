package com.example.miapp.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 25, nullable = false)
    private String nombre;

    @Column(unique = true, length = 25, nullable = false)
    private String email;

    @Column(unique = false, length = 25, nullable = false)
    private String password;

    @Column(unique = false, length = 9, nullable = false)
    private Integer telefono;

    @Column(unique = false, length = 25, nullable = false)
    private String region;

    @Column(unique = false, length = 25, nullable = false)
    private String comuna;

    @Column(columnDefinition = "TEXT")
    private String indicaciones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL , orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Orden> ordenes;

    @Column(unique = false, nullable = false)
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}