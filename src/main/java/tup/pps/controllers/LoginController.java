package tup.pps.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tup.pps.dtos.LoginResultDTO;
import tup.pps.dtos.usuarios.UsuarioLoginDTO;
import tup.pps.services.LoginService;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResultDTO> login(@RequestBody UsuarioLoginDTO loginDTO) {
        return ResponseEntity.ok(loginService.login(loginDTO));
    }
}
