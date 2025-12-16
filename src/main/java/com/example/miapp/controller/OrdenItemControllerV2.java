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

import com.example.miapp.assemblers.OrdenItemAssembler;
import com.example.miapp.model.OrdenItem;
import com.example.miapp.repository.OrdenItemRepository;


@RestController
@RequestMapping("/api/v2/orden_item")
@CrossOrigin(origins = "*")
public class  OrdenItemControllerV2 {
    @Autowired
    private OrdenItemRepository ordenItemRepository;
    
    @Autowired
    private OrdenItemAssembler assembler;
    
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<OrdenItem>> getAllOrdenItems() {
        List<EntityModel<OrdenItem>> OrdenItems = ordenItemRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(OrdenItems,
                linkTo(methodOn(OrdenItemControllerV2.class).getAllOrdenItems()).withSelfRel());
    }
    

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<OrdenItem>> createOrdenItem(@RequestBody OrdenItem prod) {
        OrdenItem newOrdenItem = ordenItemRepository.save(prod);
        return ResponseEntity
                .created(linkTo(methodOn(OrdenItemControllerV2.class).getOrdenItemByCodigo(newOrdenItem.getId())).toUri())
                .body(assembler.toModel(newOrdenItem));
    }


   @GetMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<OrdenItem> getOrdenItemByCodigo(@PathVariable Long codigo) {
        OrdenItem prod = ordenItemRepository.findById(codigo).get();
        return assembler.toModel(prod);
    }
    

    @PutMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<OrdenItem>> updateOrdenItem(@PathVariable Long codigo, @RequestBody OrdenItem prod) {
        prod.setId(codigo);
        OrdenItem updatedOrdenItem = ordenItemRepository.save(prod);
        return ResponseEntity
                .ok(assembler.toModel(updatedOrdenItem));
    }


    @DeleteMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> deleteOrdenItem(@PathVariable Long codigo) {
        ordenItemRepository.deleteById(codigo);
        return ResponseEntity.noContent().build();
    }
}

