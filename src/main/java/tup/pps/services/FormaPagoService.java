package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.models.FormaPago;

import java.util.List;

@Service
public interface FormaPagoService {
    List<FormaPago> findAll(String nombre, Boolean activo);
    FormaPago findById(Long id);
    FormaPago update(String nombreActual, String nombreNuevo);
    FormaPago save(FormaPago formaPago);
    void delete(String nombre);
}
