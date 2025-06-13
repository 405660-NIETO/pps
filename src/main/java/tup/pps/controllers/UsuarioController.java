package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tup.pps.dtos.usuarios.UsuarioRegistroDTO;
import tup.pps.dtos.usuarios.UsuarioUpdateDTO;
import tup.pps.models.Usuario;
import tup.pps.services.UsuarioService;

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
}
