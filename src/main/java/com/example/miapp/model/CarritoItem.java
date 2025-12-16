package com.example.miapp.model;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data // Lombok genera getters/setters/toString/etc.
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los campos

@Entity // Esta clase se convierte en tabla
@Table(name = "carrito_items")
public class CarritoItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")

  private Usuario usuario;


  private String sessionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "producto_id")
  private Producto producto;


  private String nombre;

  private String imagen;

  private Double precio;

  private Integer cantidad ;

}