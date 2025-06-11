package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.dtos.ProductoDTO;
import tup.pps.models.Producto;
import tup.pps.services.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/productos")
@AllArgsConstructor
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody ProductoDTO productoDTO) {
        return new ResponseEntity<>(productoService.save(productoDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(
            @PathVariable Long id,
            @RequestBody ProductoDTO productoDTO
    ) {
        return ResponseEntity.ok(productoService.update(id, productoDTO));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Producto>> getProductosByPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String nombreMarca,
            @RequestParam(required = false) List<String> categorias,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Integer stockMin,
            @RequestParam(required = false) Integer stockMax,
            @RequestParam(required = false) Boolean activo
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(productoService.findAll(
                pageable,
                nombre,
                nombreMarca,
                categorias,
                precioMin,
                precioMax,
                stockMin,
                stockMax,
                activo
        ), HttpStatus.OK);
    }
    // TODO: Agregar después los demás endpoints
    // GET /productos/page - findAll con paginado y filtros
    // GET /productos/{id} - findById
    // PUT /productos/{id} - update
    // DELETE /productos/{id} - delete
}
