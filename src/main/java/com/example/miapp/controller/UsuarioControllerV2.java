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

import com.example.miapp.assemblers.UsuarioAssembler;
import com.example.miapp.model.Usuario;
import com.example.miapp.repository.UsuarioRepository;



@RestController
@RequestMapping("/api/v2/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioControllerV2 {
    @Autowired
    private UsuarioRepository usuarioService;
    
    @Autowired
    private UsuarioAssembler assembler;
    
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Usuario>> getAllUsuarios() {
        List<EntityModel<Usuario>> envios = usuarioService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(envios,
                linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withSelfRel());
    }
    

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Usuario>> createEnvio(@RequestBody Usuario usuario) {
        Usuario newUsuario = usuarioService.save(usuario);
        return ResponseEntity
                .created(linkTo(methodOn(UsuarioControllerV2.class).getUsuarioByCodigo(newUsuario.getId())).toUri())
                .body(assembler.toModel(newUsuario));
    }


   @GetMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Usuario> getUsuarioByCodigo(@PathVariable Long codigo) {
        Usuario usuario = usuarioService.findById(codigo).get();
        return assembler.toModel(usuario);
    }
    

    @PutMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Usuario>> updateUsuario(@PathVariable Long codigo, @RequestBody Usuario usuario) {
        usuario.setId(codigo);
        Usuario updatedUsuario = usuarioService.save(usuario);
        return ResponseEntity
                .ok(assembler.toModel(updatedUsuario));
    }


    @DeleteMapping(value = "/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> deleteUsuario(@PathVariable Long codigo) {
        usuarioService.deleteById(codigo);
        return ResponseEntity.noContent().build();
    }
}
