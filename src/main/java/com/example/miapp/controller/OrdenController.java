package com.example.miapp.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.server.ResponseStatusException;

import com.example.miapp.dto.OrdenRequest;
import com.example.miapp.dto.OrdenResponse;
import com.example.miapp.model.Orden;
import com.example.miapp.model.Usuario;
import com.example.miapp.repository.OrdenRepository;
import com.example.miapp.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/orden")
@CrossOrigin(origins = "*")
@Tag(name = "Orden", description = "Operaciones sobre órdenes")
public class OrdenController {

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;

    public OrdenController(OrdenRepository ordenRepository, UsuarioRepository usuarioRepository) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @Operation(summary = "Obtiene una lista de Órdenes", description = "Obtiene un listado de todas las órdenes del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenResponse.class)))
    })
    public List<OrdenResponse> getAllOrdenes() {
        return ordenRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una Orden por ID", description = "Obtiene los datos de una orden especificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenResponse.class))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<OrdenResponse> getOrdenById(@PathVariable Long id) {
        return ordenRepository.findById(id)
                .map(orden -> ResponseEntity.ok(toResponse(orden)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Ingresa una nueva Orden", description = "Ingresa una nueva orden al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<OrdenResponse> createOrden(@RequestBody OrdenRequest req) {
        Orden orden = new Orden();

        // Asignar campos simples
        orden.setNumeroOrden(req.getNumeroOrden());
        orden.setEstado(req.getEstado() != null ? req.getEstado() : "PENDIENTE");
        orden.setTotal(req.getTotal());
        orden.setNombreCompleto(req.getNombreCompleto());
        orden.setApellidos(req.getApellidos());
        orden.setCorreo(req.getCorreo());
        orden.setCalle(req.getCalle());
        orden.setDepartamento(req.getDepartamento());
        orden.setRegion(req.getRegion());
        orden.setComuna(req.getComuna());
        orden.setIndicaciones(req.getIndicaciones());

        // Si se envía usuarioId -> asignar relación Usuario
        if (req.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(req.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no existe"));
            orden.setUsuario(usuario);
        }

        Orden saved = ordenRepository.save(orden);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza una Orden", description = "Actualiza los datos de una orden especificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenResponse.class))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<OrdenResponse> updateOrden(@PathVariable Long id, @RequestBody OrdenRequest req) {
        return ordenRepository.findById(id)
                .map(existing -> {
                    // Actualizar campos simples
                    if (req.getNumeroOrden() != null) existing.setNumeroOrden(req.getNumeroOrden());
                    if (req.getEstado() != null) existing.setEstado(req.getEstado());
                    if (req.getTotal() != null) existing.setTotal(req.getTotal());
                    if (req.getNombreCompleto() != null) existing.setNombreCompleto(req.getNombreCompleto());
                    if (req.getApellidos() != null) existing.setApellidos(req.getApellidos());
                    if (req.getCorreo() != null) existing.setCorreo(req.getCorreo());
                    if (req.getCalle() != null) existing.setCalle(req.getCalle());
                    if (req.getDepartamento() != null) existing.setDepartamento(req.getDepartamento());
                    if (req.getRegion() != null) existing.setRegion(req.getRegion());
                    if (req.getComuna() != null) existing.setComuna(req.getComuna());
                    if (req.getIndicaciones() != null) existing.setIndicaciones(req.getIndicaciones());

                    // Si se envía usuarioId -> actualizar relación Usuario
                    if (req.getUsuarioId() != null) {
                        Usuario usuario = usuarioRepository.findById(req.getUsuarioId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no existe"));
                        existing.setUsuario(usuario);
                    }

                    Orden saved = ordenRepository.save(existing);
                    return ResponseEntity.ok(toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una Orden", description = "Elimina una orden especificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Orden eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<?> deleteOrden(@PathVariable Long id) {
        return ordenRepository.findById(id)
                .map(existing -> {
                    ordenRepository.delete(existing);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Helpers ---

    private OrdenResponse toResponse(Orden orden) {
        Long usuarioId = (orden.getUsuario() != null) ? orden.getUsuario().getId() : null;
        return new OrdenResponse(
                orden.getId(),
                usuarioId,
                orden.getNumeroOrden(),
                orden.getEstado(),
                orden.getTotal(),
                orden.getNombreCompleto(),
                orden.getApellidos(),
                orden.getCorreo(),
                orden.getCalle(),
                orden.getDepartamento(),
                orden.getRegion(),
                orden.getComuna(),
                orden.getIndicaciones()
        );
    }
}