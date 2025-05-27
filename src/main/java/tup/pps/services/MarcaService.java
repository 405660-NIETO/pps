package tup.pps.services;

import org.springframework.stereotype.Service;
import tup.pps.models.Marca;

import java.util.List;

@Service
public interface MarcaService {
    List<Marca> findAll();
    Marca findById(Long id);
    Marca save(Marca marca);
    void delete(String nombre);
}
