package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.entities.UsuarioEntity;
import tup.pps.models.Usuario;

import java.util.List;
import java.util.Optional;

@Service
public interface UsuarioService {

    Optional<UsuarioEntity> findEntityById(Long id);
}
