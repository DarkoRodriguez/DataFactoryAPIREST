package com.example.miapp.assemblers;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.example.miapp.controller.OrdenItemControllerV2;
import com.example.miapp.model.OrdenItem;

@Component
public class OrdenItemAssembler implements RepresentationModelAssembler<OrdenItem, EntityModel<OrdenItem>> {

    @Override
    public EntityModel<OrdenItem> toModel(OrdenItem OrdenItem) {
        return EntityModel.of(OrdenItem,
                linkTo(methodOn(OrdenItemControllerV2.class).getOrdenItemByCodigo(OrdenItem.getId())).withSelfRel(),
                linkTo(methodOn(OrdenItemControllerV2.class).getAllOrdenItems()).withRel("OrdenItem"));
    }
}