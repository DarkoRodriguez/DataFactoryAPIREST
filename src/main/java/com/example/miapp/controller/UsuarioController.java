package com.example.miapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.example.miapp.model.Usuario;
import com.example.miapp.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*") // Permite peticiones desde frontend
@Tag(name = "Usuarios", description = "Operaciones relacionadas a los usuarios del sistema")
public class UsuarioController {
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @GetMapping
    @Operation(summary = "Obtiene a los usuarios", description = "Obtiene una lista de todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class)))
    })
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()){
            return ResponseEntity.noContent().build();        
        }
        return ResponseEntity.ok(usuarios);
    }
    
    @PostMapping
    @Operation(summary = "Ingresa nuevos usuarios", description = "Crea e ingresa nuevos datos de usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class)))
    })
    public ResponseEntity<Usuario> guardar(@RequestBody Usuario usuario) {

        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtiene usuarios por ID", description = "Obtiene los datos de un usuario especifico por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        try{
            Usuario usuario = usuarioRepository.findById(id).get();
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); 
        }
        
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualiza usuarios por ID", description = "Actualiza los datos de un usuario especificado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try{

            
            Usuario usr = usuarioRepository.findById(id).get();
            usr.setId(id);
            usr.setNombre(usuario.getNombre());
            usr.setComuna(usuario.getComuna());
            usr.setEmail(usuario.getEmail());
            usr.setPassword(usuario.getPassword());   
            usr.setFechaCreacion(usuario.getFechaCreacion());
            usr.setRegion(usuario.getRegion());
            usr.setTelefono(usuario.getTelefono());

            usuarioRepository.save(usr);
            return ResponseEntity.ok(usuario); 
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un usuario por ID", description = "Elimina al usuario especificado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try{
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
