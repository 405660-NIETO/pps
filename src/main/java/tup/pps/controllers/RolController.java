package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.models.Rol;
import tup.pps.services.RolService;

import java.util.List;

@RestController
@RequestMapping("/usuarios/roles")
@AllArgsConstructor
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping("/{id}")
    public ResponseEntity<Rol> getRolById(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Rol>> getRoles(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo
    ) {
        return ResponseEntity.ok(rolService.findAll(nombre, activo));
    }

    @PostMapping
    public ResponseEntity<Rol> createRol(@RequestBody Rol rol) {
        return new ResponseEntity<>(rolService.save(rol), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Rol> updateRol(
            @RequestParam String nombreActual,
            @RequestParam String nombreNuevo
    ) {
        return ResponseEntity.ok(rolService.update(nombreActual, nombreNuevo));
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<Void> deleteRol(@PathVariable String nombre) {
        rolService.delete(nombre);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
