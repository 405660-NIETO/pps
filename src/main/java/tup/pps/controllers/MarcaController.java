package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.models.Marca;
import tup.pps.services.MarcaService;

@RestController
@RequestMapping("/productos/marcas")
@AllArgsConstructor
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @GetMapping("/{id}")
    public ResponseEntity<Marca> getMarcaById(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.findById(id));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Marca>> getMarcasByPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(marcaService.findAll(
                pageable,
                nombre,
                activo
        ), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Marca> createMarca(@RequestBody String marca) {
        return new ResponseEntity<>(marcaService.save(marca), HttpStatus.CREATED);
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<Void> deleteMarca(@PathVariable String nombre) {
        marcaService.delete(nombre);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
