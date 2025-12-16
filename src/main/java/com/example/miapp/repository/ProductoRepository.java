package com.example.miapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.miapp.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
