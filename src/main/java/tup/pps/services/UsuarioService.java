package tup.pps.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tup.pps.dtos.usuarios.UsuarioRegistroDTO;
import tup.pps.dtos.usuarios.UsuarioUpdateDTO;
import tup.pps.entities.UsuarioEntity;
import tup.pps.models.Usuario;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public interface UsuarioService {
    Optional<UsuarioEntity> findEntityById(Long id);
    Usuario save(UsuarioRegistroDTO dto);           // Registro + reactivación
    Usuario update(Long id, UsuarioUpdateDTO dto);  // Perfil + password
    Usuario findById(Long id);                      // Para Spring Security
    Usuario findByEmail(String email);             // Login validation
    Page<Usuario> findAll(
            Pageable pageable,
            String email,
            String nombreApellido,  // Búsqueda combinada
            Long rolId,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Boolean activo
    );
    void delete(Long id);                          // Dar de baja
}
