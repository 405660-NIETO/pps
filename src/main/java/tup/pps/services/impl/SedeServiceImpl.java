package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.entities.SedeEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.ResourceAlreadyExistsException;
import tup.pps.models.Sede;
import tup.pps.repositories.SedeRepository;
import tup.pps.repositories.specs.SedeSpecification;
import tup.pps.services.SedeService;

import java.util.List;
import java.util.Optional;

@Service
public class SedeServiceImpl implements SedeService {

    @Autowired
    private SedeRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SedeSpecification specification;

    @Override
    public List<Sede> findAll(String nombre, String direccion, Boolean activo) {
        Specification<SedeEntity> spec = specification.byNombre(nombre)
                .and(specification.byDireccion(direccion))
                .and(specification.byActivo(activo));

        List<SedeEntity> entities = repository.findAll(spec);

        return entities.stream()
                .map(entity -> modelMapper.map(entity, Sede.class))
                .toList();
    }

    @Override
    public Sede findById(Long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, Sede.class))
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ninguna sede con ese ID"));
    }

    @Override
    public Sede update(String direccionActual, String nombreNuevo, String direccionNueva) {
        // 1. Verificar que existe la sede a actualizar
        SedeEntity entity = repository.findByDireccion(direccionActual)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro una sede con esa direccion"));

        // 2. Verificar que la nueva direccion no exista ya (si es diferente)
        if (!direccionActual.equals(direccionNueva)) {
            Optional<SedeEntity> existeNuevaDireccion = repository.findByDireccion(direccionNueva);
            if (existeNuevaDireccion.isPresent()) {
                throw new ResourceAlreadyExistsException("Ya existe una sede con esa direccion");
            }
        }

        // 3. Actualizar ambos campos
        entity.setNombre(nombreNuevo);
        entity.setDireccion(direccionNueva);
        entity = repository.save(entity);

        return modelMapper.map(entity, Sede.class);
    }

    @Override
    public Sede save(Sede sede) {
        Optional<SedeEntity> sedeOpcional = repository.findByDireccion(sede.getDireccion());
        if(sedeOpcional.isPresent()) {
            if(sedeOpcional.get().getActivo()) {
                throw new ResourceAlreadyExistsException("Ya hay una sede con esa direccion");
            }
            else {
                // Reactivar y actualizar nombre también
                sedeOpcional.get().setActivo(true);
                sedeOpcional.get().setNombre(sede.getNombre());
                SedeEntity entity = repository.save(sedeOpcional.get());
                return modelMapper.map(entity, Sede.class);
            }
        }

        SedeEntity entity = new SedeEntity();
        entity.setNombre(sede.getNombre());
        entity.setDireccion(sede.getDireccion());
        entity.setActivo(true);
        entity = repository.save(entity);
        return modelMapper.map(entity, Sede.class);
    }

    @Override
    public void delete(String direccion) {
        SedeEntity entity = repository.findByDireccion(direccion)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro una sede con esa direccion"));

        if(!entity.getActivo()) {
            throw new ConflictiveStateException("La sede ya esta desactivada");
        }

        entity.setActivo(false);
        repository.save(entity);
    }
}
