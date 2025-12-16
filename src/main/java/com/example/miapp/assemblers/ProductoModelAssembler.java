package com.example.miapp.assemblers;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.example.miapp.controller.ProductoControllerV2;
import com.example.miapp.model.Producto;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>> {

    @Override
    public EntityModel<Producto> toModel(Producto prod) {
        return EntityModel.of(prod,
                linkTo(methodOn(ProductoControllerV2.class).getProductoByCodigo(prod.getId())).withSelfRel(),
                linkTo(methodOn(ProductoControllerV2.class).getAllProductos()).withRel("productos"));
    }
}

