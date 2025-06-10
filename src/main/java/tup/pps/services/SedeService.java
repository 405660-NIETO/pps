package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.models.Sede;

import java.util.List;

@Service
public interface SedeService {
    List<Sede> findAll(String nombre, String direccion, Boolean activo);
    Sede findById(Long id);
    Sede update(String direccionActual, String nombreNuevo, String direccionNueva);
    Sede save(Sede sede);
    void delete(String direccion);  // Por direccion (unique)
}