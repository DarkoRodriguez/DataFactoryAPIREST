package com.example.miapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.miapp.model.OrdenItem;


public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {
}
