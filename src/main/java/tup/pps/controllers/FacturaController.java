package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.dtos.FacturaDTO;
import tup.pps.models.Factura;
import tup.pps.services.FacturaService;

import java.time.LocalDateTime;

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

    @GetMapping("/page")
    public ResponseEntity<Page<Factura>> getFacturasByPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long sedeId,
            @RequestParam(required = false) Long formaPagoId,
            @RequestParam(required = false) Double montoMin,
            @RequestParam(required = false) Double montoMax,
            @RequestParam(required = false) Boolean tieneReparaciones,
            @RequestParam(required = false) Boolean tieneProductos,
            @RequestParam(required = false) Integer cantidadItemsMin,
            @RequestParam(required = false) Boolean activo
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(facturaService.findAll(
                pageable,
                fechaDesde,
                fechaHasta,
                usuarioId,
                sedeId,
                formaPagoId,
                montoMin,
                montoMax,
                tieneReparaciones,
                tieneProductos,
                cantidadItemsMin,
                activo
        ), HttpStatus.OK);
    }
}