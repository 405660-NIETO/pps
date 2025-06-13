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
import tup.pps.dtos.usuarios.UsuarioRegistroDTO;
import tup.pps.dtos.usuarios.UsuarioUpdateDTO;
import tup.pps.models.Usuario;
import tup.pps.services.UsuarioService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody UsuarioRegistroDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.save(usuarioDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateDTO usuarioDTO
    ) {
        return ResponseEntity.ok(usuarioService.update(id, usuarioDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Usuario>> getUsuariosByPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long rolId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) Boolean activo
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(usuarioService.findAll(
                pageable,
                email,
                nombre,
                rolId,
                fechaDesde,
                fechaHasta,
                activo
        ), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
