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

import com.example.miapp.assemblers.OrdenAssembler;
import com.example.miapp.model.Orden;
import com.example.miapp.repository.OrdenRepository;


@RestController
@RequestMapping("/api/v2/orden")
@CrossOrigin(origins = "*")
public class  OrdenControllerV2 {
    @Autowired
    private OrdenRepository ordenRepository;
    
    @Autowired
    private OrdenAssembler assembler;
    
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Orden>> getAllOrdens() {
        List<EntityModel<Orden>> Ordens = ordenRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(Ordens,
                linkTo(methodOn(OrdenControllerV2.class).getAllOrdens()).withSelfRel());
    }
    

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Orden>> createOrden(@RequestBody Orden prod) {
        Orden newOrden = ordenRepository.save(prod);
        return ResponseEntity
                .created(linkTo(methodOn(OrdenControllerV2.class).getOrdenByCodigo(newOrden.getId())).toUri())
                .body(assembler.toModel(newOrden));
    }


   @GetMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Orden> getOrdenByCodigo(@PathVariable Long codigo) {
        Orden prod = ordenRepository.findById(codigo).get();
        return assembler.toModel(prod);
    }
    

    @PutMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Orden>> updateOrden(@PathVariable Long codigo, @RequestBody Orden prod) {
        prod.setId(codigo);
        Orden updatedOrden = ordenRepository.save(prod);
        return ResponseEntity
                .ok(assembler.toModel(updatedOrden));
    }


    @DeleteMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> deleteOrden(@PathVariable Long codigo) {
        ordenRepository.deleteById(codigo);
        return ResponseEntity.noContent().build();
    }
}

