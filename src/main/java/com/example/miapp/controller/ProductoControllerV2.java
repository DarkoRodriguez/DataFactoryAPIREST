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

import com.example.miapp.assemblers.ProductoModelAssembler;
import com.example.miapp.model.Producto;
import com.example.miapp.repository.ProductoRepository;


@RestController
@RequestMapping("/api/v2/producto")
@CrossOrigin(origins = "*")
public class  ProductoControllerV2 {
    @Autowired
    private ProductoRepository productoService;
    
    @Autowired
    private ProductoModelAssembler assembler;
    
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Producto>> getAllProductos() {
        List<EntityModel<Producto>> productos = productoService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoControllerV2.class).getAllProductos()).withSelfRel());
    }
    

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Producto>> createProducto(@RequestBody Producto prod) {
        Producto newProducto = productoService.save(prod);
        return ResponseEntity
                .created(linkTo(methodOn(ProductoControllerV2.class).getProductoByCodigo(newProducto.getId())).toUri())
                .body(assembler.toModel(newProducto));
    }


   @GetMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Producto> getProductoByCodigo(@PathVariable Long codigo) {
        Producto prod = productoService.findById(codigo).get();
        return assembler.toModel(prod);
    }
    

    @PutMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Producto>> updateProducto(@PathVariable Long codigo, @RequestBody Producto prod) {
        prod.setId(codigo);
        Producto updatedProducto = productoService.save(prod);
        return ResponseEntity
                .ok(assembler.toModel(updatedProducto));
    }


    @DeleteMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> deleteProducto(@PathVariable Long codigo) {
        productoService.deleteById(codigo);
        return ResponseEntity.noContent().build();
    }
}

