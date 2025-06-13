package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.dtos.usuarios.UsuarioRegistroDTO;
import tup.pps.dtos.usuarios.UsuarioUpdateDTO;
import tup.pps.entities.UsuarioEntity;
import tup.pps.models.Usuario;

import java.util.List;
import java.util.Optional;

@Service
public interface UsuarioService {
    Optional<UsuarioEntity> findEntityById(Long id);
    Usuario save(UsuarioRegistroDTO dto);           // Registro + reactivación
    Usuario update(Long id, UsuarioUpdateDTO dto);  // Perfil + password
    Usuario findById(Long id);                      // Para Spring Security
    Usuario findByEmail(String email);             // Login validation
    List<Usuario> findAll(String nombre, Boolean activo);  // Admin management
    void delete(Long id);                          // Dar de baja
}
