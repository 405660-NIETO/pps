package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.models.Rol;

import java.util.List;

@Service
public interface RolService {
    List<Rol> findAll(String nombre, Boolean activo);
    Rol findById(Long id);
    Rol update(String nombreActual, String nombreNuevo);
    Rol save(Rol rol);
    void delete(String nombre);
}
