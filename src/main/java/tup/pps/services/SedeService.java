package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.dtos.SedeDTO;
import tup.pps.entities.SedeEntity;
import tup.pps.models.Sede;

import java.util.List;
import java.util.Optional;

@Service
public interface SedeService {
    List<Sede> findAll(String nombre, String direccion, Boolean activo);
    Sede findById(Long id);
    Sede update(String direccionActual, SedeDTO sedeDTO);
    Sede save(SedeDTO sede);
    void delete(String direccion);
    Optional<SedeEntity> findByDireccion(String direccion);
}