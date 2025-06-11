package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.dtos.ReparacionDTO;
import tup.pps.models.Reparacion;
import tup.pps.services.ReparacionService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reparaciones")
@AllArgsConstructor
public class ReparacionController {

    @Autowired
    private ReparacionService reparacionService;

    @PostMapping
    public ResponseEntity<Reparacion> createReparacion(@RequestBody ReparacionDTO reparacionDTO) {
        return new ResponseEntity<>(reparacionService.save(reparacionDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reparacion> updateReparacion(
            @PathVariable Long id,
            @RequestBody ReparacionDTO reparacionDTO
    ) {
        return ResponseEntity.ok(reparacionService.update(id, reparacionDTO));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Reparacion>> getReparacionesByPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) List<String> trabajos,
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaEntrega,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Boolean activo
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(reparacionService.findAll(
                pageable,
                usuario,
                trabajos,
                fechaInicio,
                fechaEntrega,
                precioMin,
                precioMax,
                activo
        ), HttpStatus.OK);
    }

}
