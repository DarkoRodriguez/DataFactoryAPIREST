package com.example.miapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miapp.model.CarritoItem;



public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
}
