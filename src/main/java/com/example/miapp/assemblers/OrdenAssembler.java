package com.example.miapp.assemblers;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.example.miapp.controller.OrdenControllerV2;
import com.example.miapp.model.Orden;

@Component
public class OrdenAssembler implements RepresentationModelAssembler<Orden, EntityModel<Orden>> {

    @Override
    public EntityModel<Orden> toModel(Orden Orden) {
        return EntityModel.of(Orden,
                linkTo(methodOn(OrdenControllerV2.class).getOrdenByCodigo(Orden.getId())).withSelfRel(),
                linkTo(methodOn(OrdenControllerV2.class).getAllOrdens()).withRel("Orden"));
    }
}