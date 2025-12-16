package com.example.miapp.assemblers;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.example.miapp.controller.CarritoItemControllerV2;
import com.example.miapp.model.CarritoItem;

@Component
public class CarritoItemAssembler implements RepresentationModelAssembler<CarritoItem, EntityModel<CarritoItem>> {

    @Override
    public EntityModel<CarritoItem> toModel(CarritoItem CarritoItem) {
        return EntityModel.of(CarritoItem,
                linkTo(methodOn(CarritoItemControllerV2.class).CarritoItemByCodigo(CarritoItem.getId())).withSelfRel(),
                linkTo(methodOn(CarritoItemControllerV2.class).CarritoItems()).withRel("CarritoItem"));
    }
}