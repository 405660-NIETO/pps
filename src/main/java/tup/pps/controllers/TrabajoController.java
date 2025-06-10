package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.models.Trabajo;
import tup.pps.services.TrabajoService;

@RestController
@RequestMapping("/reparaciones/trabajos")
@AllArgsConstructor
public class TrabajoController {

    @Autowired
    private TrabajoService trabajoService;

    @GetMapping("/{id}")
    public ResponseEntity<Trabajo> getTrabajoById(@PathVariable Long id) {
        return ResponseEntity.ok(trabajoService.findById(id));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Trabajo>> getTrabajosByPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(trabajoService.findAll(
                pageable,
                nombre,
                activo
        ), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Trabajo> createTrabajo(@RequestBody Trabajo trabajo) {
        return new ResponseEntity<>(trabajoService.save(trabajo), HttpStatus.CREATED);
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<Void> deleteTrabajo(@PathVariable String nombre) {
        trabajoService.delete(nombre);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
