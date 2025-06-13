package tup.pps.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import tup.pps.entities.UsuarioEntity;
import tup.pps.exceptions.ForbiddenOperationException;

import java.time.LocalDateTime;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        UsuarioEntity usuario = usuarioService.findByEmail(email);

        // Usuario inactivo
        if (!usuario.getActivo()) {
            throw new ForbiddenOperationException("Usuario inactivo");
        }

        // Refrescar fecha login
        usuario.setFechaLogin(LocalDateTime.now());
        usuarioService.actualizarUsuario(usuario);

        return User.builder()
                .username(usuario.getEmail())
                .password("{noop}" + usuario.getPassword())  // ← {noop} = no encryption
                .roles(usuario.getRol().getNombre())
                .build();
    }
}