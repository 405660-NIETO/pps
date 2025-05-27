package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.models.Usuario;

import java.util.List;

@Service
public interface UsuarioService {
    List<Usuario> findAll();
    Usuario findById(Long id);
    Usuario save(Usuario usuario);
    Usuario update(Usuario usuario);
    void delete(Long id);
}
