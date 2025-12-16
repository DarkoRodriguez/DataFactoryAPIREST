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

import com.example.miapp.dto.OrdenItemRequest;
import com.example.miapp.dto.OrdenItemResponse;
import com.example.miapp.model.Orden;
import com.example.miapp.model.OrdenItem;
import com.example.miapp.model.Producto;
import com.example.miapp.repository.OrdenItemRepository;
import com.example.miapp.repository.OrdenRepository;
import com.example.miapp.repository.ProductoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/orden_item")
@CrossOrigin(origins = "*")
@Tag(name = "OrdenItem", description = "Operaciones sobre items de órdenes")
public class OrdenItemController {

    private final OrdenItemRepository ordenItemRepository;
    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;

    public OrdenItemController(OrdenItemRepository ordenItemRepository,
                               OrdenRepository ordenRepository,
                               ProductoRepository productoRepository) {
        this.ordenItemRepository = ordenItemRepository;
        this.ordenRepository = ordenRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    @Operation(summary = "Obtiene una lista de OrdenItems", description = "Obtiene un listado de los OrdenItems del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenItemResponse.class)))
    })
    public List<OrdenItemResponse> getAllOrdenItems() {
        return ordenItemRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un OrdenItem por ID", description = "Obtiene los datos de un OrdenItem especificado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "OrdenItem no encontrado")
    })
    public ResponseEntity<OrdenItemResponse> getOrdenItemById(@PathVariable Long id) {
        return ordenItemRepository.findById(id)
                .map(oi -> ResponseEntity.ok(toResponse(oi)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Ingresa un nuevo OrdenItem", description = "Ingresa un nuevo OrdenItem al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OrdenItem creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<OrdenItemResponse> createOrdenItem(@RequestBody OrdenItemRequest req) {
        OrdenItem oi = new OrdenItem();

        // Asignar campos simples
        oi.setNombre(req.getNombre());
        oi.setCantidad(req.getCantidad());
        oi.setPrecioUnitario(req.getPrecioUnitario());
        oi.setSubtotal(req.getSubtotal());

        // Si se envía ordenId -> asignar relación Orden
        if (req.getOrdenId() != null) {
            Orden orden = ordenRepository.findById(req.getOrdenId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Orden no existe"));
            oi.setOrden(orden);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ordenId es requerido");
        }

        // Si se envía productoId -> asignar relación Producto
        if (req.getProductoId() != null) {
            Producto producto = productoRepository.findById(req.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no existe"));
            oi.setProducto(producto);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productoId es requerido");
        }

        OrdenItem saved = ordenItemRepository.save(oi);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un OrdenItem", description = "Actualiza los datos de un OrdenItem especificado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OrdenItem actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenItemResponse.class))),
            @ApiResponse(responseCode = "404", description = "OrdenItem no encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<OrdenItemResponse> updateOrdenItem(@PathVariable Long id, @RequestBody OrdenItemRequest req) {
        return ordenItemRepository.findById(id)
                .map(existing -> {
                    // Actualizar campos simples
                    if (req.getNombre() != null) existing.setNombre(req.getNombre());
                    if (req.getCantidad() != null) existing.setCantidad(req.getCantidad());
                    if (req.getPrecioUnitario() != null) existing.setPrecioUnitario(req.getPrecioUnitario());
                    if (req.getSubtotal() != null) existing.setSubtotal(req.getSubtotal());

                    // Si se envía ordenId -> actualizar relación Orden
                    if (req.getOrdenId() != null) {
                        Orden orden = ordenRepository.findById(req.getOrdenId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Orden no existe"));
                        existing.setOrden(orden);
                    }

                    // Si se envía productoId -> actualizar relación Producto
                    if (req.getProductoId() != null) {
                        Producto producto = productoRepository.findById(req.getProductoId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no existe"));
                        existing.setProducto(producto);
                    }

                    OrdenItem saved = ordenItemRepository.save(existing);
                    return ResponseEntity.ok(toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un OrdenItem", description = "Elimina un OrdenItem especificado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OrdenItem eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "OrdenItem no encontrado")
    })
    public ResponseEntity<?> deleteOrdenItem(@PathVariable Long id) {
        return ordenItemRepository.findById(id)
                .map(existing -> {
                    ordenItemRepository.delete(existing);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Helpers ---

    private OrdenItemResponse toResponse(OrdenItem oi) {
        Long ordenId = (oi.getOrden() != null) ? oi.getOrden().getId() : null;
        Long productoId = (oi.getProducto() != null) ? oi.getProducto().getId() : null;

        return new OrdenItemResponse(
                oi.getId(),
                ordenId,
                productoId,
                oi.getNombre(),
                oi.getCantidad(),
                oi.getPrecioUnitario(),
                oi.getSubtotal()
        );
    }
}