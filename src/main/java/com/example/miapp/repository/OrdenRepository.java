package com.example.miapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miapp.model.Orden;


public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
