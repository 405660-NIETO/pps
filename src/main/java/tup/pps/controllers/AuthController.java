package tup.pps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tup.pps.dtos.usuarios.UsuarioDTO;
import tup.pps.entities.UsuarioEntity;
import tup.pps.services.UsuarioService;

/**
 * AuthController - Endpoints específicos de autenticación
 *
 * Separado de UsuarioController para mantener responsabilidades claras:
 * - AuthController: Información de sesión actual
 * - UsuarioController: CRUD administrativo de usuarios
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * GET /auth/me
     *
     * Obtiene información del usuario actualmente autenticado.
     * Se ejecuta automáticamente después de login para cargar datos en el frontend.
     *
     * @param authentication Inyectado automáticamente por Spring Security
     * @return UsuarioDTO con datos del usuario actual (sin password)
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getCurrentUser(Authentication authentication) {
        // 1. Obtener email del usuario autenticado (viene de Spring Security)
        String email = authentication.getName();

        // 2. Buscar usuario completo por email
        UsuarioEntity usuario = usuarioService.findByEmail(email);

        // 3. Convertir a DTO (sin password, solo datos necesarios para frontend)
        UsuarioDTO usuarioDTO = new UsuarioDTO(
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getRol().getNombre()
        );

        return ResponseEntity.ok(usuarioDTO);
    }
}