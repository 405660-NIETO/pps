package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tup.pps.dtos.FacturaDTO;
import tup.pps.models.Factura;
import tup.pps.services.FacturaService;

@RestController
@RequestMapping("/facturas")
@AllArgsConstructor
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @PostMapping
    public ResponseEntity<Factura> createFactura(@RequestBody FacturaDTO facturaDTO) {
        return new ResponseEntity<>(facturaService.save(facturaDTO), HttpStatus.CREATED);
    }
}