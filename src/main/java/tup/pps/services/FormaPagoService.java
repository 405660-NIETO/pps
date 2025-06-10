package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.entities.FormaPagoEntity;
import tup.pps.models.FormaPago;

import java.util.List;
import java.util.Optional;

@Service
public interface FormaPagoService {
    List<FormaPago> findAll(String nombre, Boolean activo);
    FormaPago findById(Long id);
    FormaPago update(String nombreActual, String nombreNuevo);
    FormaPago save(String formaPago);
    void delete(String nombre);
    Optional<FormaPagoEntity> findByNombre(String nombre);
}
