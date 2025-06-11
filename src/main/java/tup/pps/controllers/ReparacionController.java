package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tup.pps.dtos.ReparacionDTO;
import tup.pps.models.Reparacion;
import tup.pps.services.ReparacionService;

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

}
