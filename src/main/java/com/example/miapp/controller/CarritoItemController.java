package com.example.miapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
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

import com.example.miapp.dto.CarritoItemRequest;
import com.example.miapp.dto.CarritoItemResponse;
import com.example.miapp.model.CarritoItem;
import com.example.miapp.model.Producto;
import com.example.miapp.model.Usuario;
import com.example.miapp.repository.CarritoItemRepository;
import com.example.miapp.repository.ProductoRepository;
import com.example.miapp.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/carrito_item")
@CrossOrigin(origins = "*")
@Tag(name = "CarritoItem", description = "Operaciones sobre items del carrito")
public class CarritoItemController {

    private final CarritoItemRepository carritoRepo;
    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;

    public CarritoItemController(CarritoItemRepository carritoRepo,
                                 ProductoRepository productoRepo,
                                 UsuarioRepository usuarioRepo) {
        this.carritoRepo = carritoRepo;
        this.productoRepo = productoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Operation(summary = "Listar todos los items del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de items obtenida correctamente")
    })
    @GetMapping
    public List<CarritoItemResponse> getAllCarritoItems() {
        return carritoRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener un item del carrito por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item encontrado"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CarritoItemResponse> getCarritoItemById(@PathVariable Long id) {
        return carritoRepo.findById(id)
                .map(ci -> ResponseEntity.ok(toResponse(ci)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo item en el carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Item creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<CarritoItemResponse> createCarritoItem(@RequestBody CarritoItemRequest req) {

        // 1) Validación mínima: se requiere producto_id (CarritoItemRequest.getProductoId() resuelve wrappers)
        Long productoId = req.getProductoId();
        if (productoId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "producto_id es requerido");
        }

        // 2) Resolver Producto
        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no existe: " + productoId));

        // 3) Resolver Usuario si viene usuarioId
        Usuario usuario = null;
        if (req.getUsuarioId() != null) {
            usuario = usuarioRepo.findById(req.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no existe: " + req.getUsuarioId()));
        }

        // 4) Construir entidad y poblar campos
        CarritoItem ci = new CarritoItem();
        ci.setProducto(producto);
        if (usuario != null) ci.setUsuario(usuario);
        ci.setSessionId(req.getSessionId());
        ci.setCantidad(req.getCantidad() != null ? req.getCantidad() : 1);
        ci.setNombre(req.getNombre());
        ci.setImagen(req.getImagen());
        ci.setPrecio(req.getPrecio());

        // 5) Guardar protegiendo errores de BD
        try {
            CarritoItem saved = carritoRepo.save(ci);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
        } catch (DataAccessException dae) {
            // Retornar 500 con mensaje de causa específica para debug (no en producción revelar stack completo)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al persistir carrito_item: " + (dae.getMostSpecificCause() != null ? dae.getMostSpecificCause().getMessage() : dae.getMessage()), dae);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado: " + e.getMessage(), e);
        }
    }

    @Operation(summary = "Actualizar un item del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item actualizado"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CarritoItemResponse> updateCarritoItem(@PathVariable Long id,
                                                                  @RequestBody CarritoItemRequest req) {
        return carritoRepo.findById(id)
                .map(existing -> {
                    // Actualizar campos permitidos
                    applyRequestToEntity(req, existing);
                    CarritoItem saved = carritoRepo.save(existing);
                    return ResponseEntity.ok(toResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un item del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Item eliminado"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarritoItem(@PathVariable Long id) {
        return carritoRepo.findById(id)
                .map(existing -> {
                    carritoRepo.delete(existing);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Helpers ---

    private CarritoItemResponse toResponse(CarritoItem ci) {
        Long productoId = (ci.getProducto() != null) ? ci.getProducto().getId() : null;
        Long usuarioId = (ci.getUsuario() != null) ? ci.getUsuario().getId() : null;

        return new CarritoItemResponse(
                ci.getId(),
                productoId,
                usuarioId,
                ci.getSessionId(),
                ci.getCantidad(),
                ci.getNombre(),
                ci.getPrecio(),
                ci.getImagen()
        );
    }

    private void applyRequestToEntity(CarritoItemRequest req, CarritoItem ci) {
        // Mantenemos comportamiento previo para PUT/patch parcial
        if (req.getCantidad() != null) {
            ci.setCantidad(req.getCantidad());
        }
        if (req.getNombre() != null) {
            ci.setNombre(req.getNombre());
        }
        if (req.getImagen() != null) {
            ci.setImagen(req.getImagen());
        }
        if (req.getPrecio() != null) {
            ci.setPrecio(req.getPrecio());
        }
        if (req.getSessionId() != null) {
            ci.setSessionId(req.getSessionId());
        }
        if (req.getProductoId() != null) {
            Producto prod = productoRepo.findById(req.getProductoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no existe"));
            ci.setProducto(prod);
        }
        if (req.getUsuarioId() != null) {
            Usuario u = usuarioRepo.findById(req.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no existe"));
            ci.setUsuario(u);
        }
    }
}