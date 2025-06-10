package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tup.pps.dtos.ProductoDTO;
import tup.pps.models.Producto;
import tup.pps.services.ProductoService;

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

    // TODO: Agregar después los demás endpoints
    // GET /productos/page - findAll con paginado y filtros
    // GET /productos/{id} - findById
    // PUT /productos/{id} - update
    // DELETE /productos/{id} - delete
}
