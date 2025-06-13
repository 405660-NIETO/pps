package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.dtos.SedeDTO;
import tup.pps.dtos.SedeUpdateDTO;
import tup.pps.models.Sede;
import tup.pps.services.SedeService;

import java.util.List;

@RestController
@RequestMapping("/sedes")
@AllArgsConstructor
public class SedeController {

    @Autowired
    private SedeService sedeService;

    @GetMapping("/{id}")
    public ResponseEntity<Sede> getSedeById(@PathVariable Long id) {
        return ResponseEntity.ok(sedeService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Sede>> getSedes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) Boolean activo
    ) {
        return ResponseEntity.ok(sedeService.findAll(nombre, direccion, activo));
    }

    @PostMapping
    public ResponseEntity<Sede> createSede(@RequestBody SedeDTO sede) {
        return new ResponseEntity<>(sedeService.save(sede), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Sede> updateSede(
            @RequestBody SedeUpdateDTO sedeUpdateDTO
    ) {
        SedeDTO dto = new SedeDTO(sedeUpdateDTO.getNombre(), sedeUpdateDTO.getDireccion());
        return ResponseEntity.ok(sedeService.update(sedeUpdateDTO.getDireccionActual(), dto));
    }

    @DeleteMapping("/{direccion}")
    public ResponseEntity<Void> deleteSede(@PathVariable String direccion) {
        sedeService.delete(direccion);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}