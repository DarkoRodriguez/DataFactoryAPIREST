package com.example.miapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miapp.model.Orden;


import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
	Optional<Orden> findByNumeroOrden(String numeroOrden);
}
