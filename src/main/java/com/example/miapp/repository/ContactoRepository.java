package com.example.miapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.miapp.model.Contacto;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    // queries custom si hace falta, por ahora básico
}