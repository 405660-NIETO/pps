package tup.pps.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.dtos.LoginResultDTO;
import tup.pps.dtos.usuarios.UsuarioLoginDTO;
import tup.pps.entities.UsuarioEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.UnauthorizedException;
import tup.pps.models.Usuario;
import tup.pps.services.LoginService;
import tup.pps.services.UsuarioService;

import java.time.LocalDateTime;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public LoginResultDTO login(UsuarioLoginDTO loginDTO) {
        // 1. BUSCAR usuario por email
        Usuario usuario = usuarioService.findByEmail(loginDTO.getEmail());

        // 2. VALIDAR password
        if (!usuario.getPassword().equals(loginDTO.getPassword())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        // 3. VALIDAR que esté activo
        if (!usuario.getActivo()) {
            throw new ConflictiveStateException("Usuario inactivo");
        }

        // 4. ACTUALIZAR fecha login (auditoría)
        actualizarFechaLogin(usuario);

        // 5. CREAR respuesta segura (sin password)
        return crearLoginResult(usuario);
    }

    // Metodos para login
    private void actualizarFechaLogin(Usuario usuario) {
        // Buscar entity para actualizar
        UsuarioEntity entity = usuarioService.findEntityById(usuario.getId())
                .orElseThrow(() -> new EntryNotFoundException("Usuario no encontrado"));

        // Actualizar fecha login
        entity.setFechaLogin(LocalDateTime.now());

        // Guardar usando el método que creaste
        usuarioService.actualizarUsuario(entity);
    }

    private LoginResultDTO crearLoginResult(Usuario usuario) {
        LoginResultDTO result = new LoginResultDTO();
        result.setEmail(usuario.getEmail());
        result.setNombre(usuario.getNombre());
        result.setApellido(usuario.getApellido());
        result.setFechaLogin(LocalDateTime.now()); // Fecha actual del login
        result.setRol(usuario.getRol().getNombre()); // Nombre del rol para frontend

        return result;
    }
}
