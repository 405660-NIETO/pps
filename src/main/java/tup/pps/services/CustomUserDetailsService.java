package tup.pps.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import tup.pps.models.Usuario;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioService.findByEmail(email);
        return User.builder()
                .username(usuario.getEmail())
                .password("{noop}" + usuario.getPassword())  // ← {noop} = no encryption
                .roles(usuario.getRol().getNombre())
                .build();
    }
}