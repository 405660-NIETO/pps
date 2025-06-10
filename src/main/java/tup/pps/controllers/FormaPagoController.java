package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.models.FormaPago;
import tup.pps.services.FormaPagoService;

import java.util.List;

@RestController
@RequestMapping("/facturas/formapago")
@AllArgsConstructor
public class FormaPagoController {

    @Autowired
    private FormaPagoService formaPagoService;

    @GetMapping("/{id}")
    public ResponseEntity<FormaPago> getFormaPagoById(@PathVariable Long id) {
        return ResponseEntity.ok(formaPagoService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<FormaPago>> getFormasPago(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo
    ) {
        return ResponseEntity.ok(formaPagoService.findAll(nombre, activo));
    }

    @PostMapping
    public ResponseEntity<FormaPago> createFormaPago(@RequestBody String formaPago) {
        return new ResponseEntity<>(formaPagoService.save(formaPago), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FormaPago> updateFormaPago(
            @RequestParam String nombreActual,
            @RequestParam String nombreNuevo
    ) {
        return ResponseEntity.ok(formaPagoService.update(nombreActual, nombreNuevo));
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<Void> deleteFormaPago(@PathVariable String nombre) {
        formaPagoService.delete(nombre);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
