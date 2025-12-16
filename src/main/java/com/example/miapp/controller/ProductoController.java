package com.example.miapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miapp.model.Producto;
import com.example.miapp.repository.ProductoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/producto")
@CrossOrigin(origins = "*") // Permite peticiones desde frontend
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Obtener todos los productos
    @GetMapping
    @Operation(summary = "Obtiene una lista de productos", description = "Obtiene un listado de los productos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class)))
    })
    public List<Producto> getAllproductos() {
        return productoRepository.findAll();
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene productos por ID", description = "Obtiene los datos de un producto especificado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public Producto getproductoById(@PathVariable Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("producto not found"));
    }

    // Crear un nuevo PRODUCTO
    @PostMapping
    @Operation(summary = "Ingresa nuevos productos", description = "Ingresa nuevos productos al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class)))
    })
    public Producto createproducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    // Actualizar producto existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualiza productos", description = "Actualiza los datos de un producto especificado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public Producto updateproducto(@PathVariable Long id, @RequestBody Producto productoDetails) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("producto not found"));
        producto.setId(productoDetails.getId());
        producto.setCategoria(productoDetails.getCategoria());
        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setDescuento(productoDetails.getDescuento());
        producto.setImagen(productoDetails.getImagen());
        producto.setNombre(productoDetails.getNombre());
        producto.setPrecio(productoDetails.getPrecio());
        producto.setOferta(productoDetails.isOferta());
        return productoRepository.save(producto);
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un producto por ID", description = "Elimina un producto especificado por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public void deleteproducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }
}
