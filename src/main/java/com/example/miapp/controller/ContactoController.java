package com.example.miapp.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miapp.dto.ContactoRequest;
import com.example.miapp.dto.ContactoResponse;
import com.example.miapp.model.Contacto;
import com.example.miapp.repository.ContactoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/contacto")
@CrossOrigin(origins = "*")
@Tag(name = "Contactos", description = "Operaciones para enviar y consultar mensajes de contacto")
public class ContactoController {

    private final ContactoRepository contactoRepository;

    public ContactoController(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    @PostMapping
    @Operation(summary = "Crear contacto", description = "Crea un nuevo mensaje de contacto.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Contacto creado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ContactoResponse> createContacto(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Payload para crear un contacto",
                required = true,
                content = @Content(schema = @Schema(implementation = ContactoRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody ContactoRequest req) {

        Contacto c = new Contacto();
        c.setNombre(req.getNombre());
        c.setEmail(req.getEmail());
        c.setMensaje(req.getMensaje());
        Contacto saved = contactoRepository.save(c);
        ContactoResponse resp = new ContactoResponse(saved.getId(), saved.getNombre(), saved.getEmail(),
                saved.getMensaje(), saved.getFechaCreacion());
        return ResponseEntity.created(URI.create("/api/v1/contacto/" + saved.getId())).body(resp);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar contacto", description = "Actualiza un mensaje de contacto existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contacto actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Contacto no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ContactoResponse> updateContacto(
            @Parameter(description = "ID del contacto", required = true) @PathVariable Long id,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Payload para actualizar contacto",
                required = true,
                content = @Content(schema = @Schema(implementation = ContactoRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody ContactoRequest req) {

        return contactoRepository.findById(id).map(existing -> {
            existing.setNombre(req.getNombre());
            existing.setEmail(req.getEmail());
            existing.setMensaje(req.getMensaje());
            Contacto saved = contactoRepository.save(existing);
            ContactoResponse resp = new ContactoResponse(saved.getId(), saved.getNombre(), saved.getEmail(),
                    saved.getMensaje(), saved.getFechaCreacion());
            return ResponseEntity.ok(resp);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar contactos", description = "Devuelve todos los mensajes de contacto.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de contactos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactoResponse.class)))
    })
    @GetMapping
    public List<ContactoResponse> listAll() {
        return contactoRepository.findAll().stream()
                .map(c -> new ContactoResponse(c.getId(), c.getNombre(), c.getEmail(), c.getMensaje(),
                        c.getFechaCreacion()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener contacto por id", description = "Obtiene un mensaje de contacto por su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contacto encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Contacto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContactoResponse> getById(
            @Parameter(description = "ID del contacto", required = true) @PathVariable Long id) {
        return contactoRepository.findById(id)
                .map(c -> ResponseEntity.ok(new ContactoResponse(c.getId(), c.getNombre(), c.getEmail(), c.getMensaje(),
                        c.getFechaCreacion())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar contacto", description = "Elimina un mensaje de contacto por su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Contacto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID del contacto", required = true) @PathVariable Long id) {
        if (!contactoRepository.existsById(id))
            return ResponseEntity.notFound().build();
        contactoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}