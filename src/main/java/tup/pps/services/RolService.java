package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.entities.RolEntity;
import tup.pps.models.Rol;

import java.util.List;
import java.util.Optional;

@Service
public interface RolService {
    List<Rol> findAll(String nombre, Boolean activo);
    Rol findById(Long id);
    Rol update(String nombreActual, String nombreNuevo);
    Rol save(String rol);
    void delete(String nombre);
    Optional<RolEntity> findByNombre(String nombre);
}
