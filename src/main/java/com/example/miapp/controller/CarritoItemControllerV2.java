package com.example.miapp.controller;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miapp.assemblers.CarritoItemAssembler;
import com.example.miapp.model.CarritoItem;
import com.example.miapp.repository.CarritoItemRepository;


@RestController
@RequestMapping("/api/v2/carrito_item")
@CrossOrigin(origins = "*")
public class  CarritoItemControllerV2 {
    @Autowired
    private CarritoItemRepository CarritoItemRepository;
    
    @Autowired
    private CarritoItemAssembler assembler;
    
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<CarritoItem>> CarritoItems() {
        List<EntityModel<CarritoItem>> CarritoItems = CarritoItemRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(CarritoItems,
                linkTo(methodOn(CarritoItemControllerV2.class).CarritoItems()).withSelfRel());
    }
    

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CarritoItem>> CarritoItem(@RequestBody CarritoItem prod) {
        CarritoItem CarritoItem = CarritoItemRepository.save(prod);
        return ResponseEntity
                .created(linkTo(methodOn(CarritoItemControllerV2.class).CarritoItemByCodigo(CarritoItem.getId())).toUri())
                .body(assembler.toModel(CarritoItem));
    }


   @GetMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<CarritoItem> CarritoItemByCodigo(@PathVariable Long codigo) {
        CarritoItem prod = CarritoItemRepository.findById(codigo).get();
        return assembler.toModel(prod);
    }
    

    @PutMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<CarritoItem>> CarritoItem(@PathVariable Long codigo, @RequestBody CarritoItem prod) {
        prod.setId(codigo);
        CarritoItem CarritoItem = CarritoItemRepository.save(prod);
        return ResponseEntity
                .ok(assembler.toModel(CarritoItem));
    }


    @DeleteMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> CarritoItem(@PathVariable Long codigo) {
        CarritoItemRepository.deleteById(codigo);
        return ResponseEntity.noContent().build();
    }
}

