package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.entities.RolEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.ResourceAlreadyExistsException;
import tup.pps.models.Rol;
import tup.pps.repositories.RolRepository;
import tup.pps.repositories.specs.RolSpecification;
import tup.pps.services.RolService;

import java.util.List;
import java.util.Optional;

/**
 * Tabla Soporte - Patrón B
 * Bajo volumen, sin paginado, con update
 */
@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RolSpecification specification;

    @Override
    public List<Rol> findAll(String nombre, Boolean activo) {
        Specification<RolEntity> spec = specification.byNombre(nombre)
                .and(specification.byActivo(activo));

        List<RolEntity> entities = repository.findAll(spec);

        return entities.stream()
                .map(entity -> modelMapper.map(entity, Rol.class))
                .toList();
    }

    @Override
    public Rol findById(Long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, Rol.class))
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ningun rol con ese ID"));
    }

    @Override
    public Rol update(String nombreActual, String nombreNuevo) {
        // 1. Verificar que existe el registro a actualizar
        RolEntity entity = repository.findByNombre(nombreActual)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro un rol con ese nombre"));

        // 2. Verificar que no esté intentando cambiar al mismo nombre
        if (nombreActual.equals(nombreNuevo)) {
            throw new ConflictiveStateException("El nuevo nombre debe ser diferente al actual");
        }

        // 3. Verificar que el nuevo nombre no exista ya
        Optional<RolEntity> existeNuevoNombre = repository.findByNombre(nombreNuevo);
        if (existeNuevoNombre.isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un rol con ese nombre");
        }

        // 4. Actualizar y guardar
        entity.setNombre(nombreNuevo);
        entity = repository.save(entity);

        return modelMapper.map(entity, Rol.class);
    }

    @Override
    public Rol save(String rol) {
        Optional<RolEntity> rolOpcional = repository.findByNombre(rol);
        if(rolOpcional.isPresent()) {
            if(rolOpcional.get().getActivo()) {
                throw new ResourceAlreadyExistsException("Ya hay un rol con ese nombre");
            }
            else {
                rolOpcional.get().setActivo(true);
                RolEntity entity = repository.save(rolOpcional.get());
                return modelMapper.map(entity, Rol.class);
            }
        }

        RolEntity entity = new RolEntity();
        entity.setNombre(rol);
        entity.setActivo(true);
        entity = repository.save(entity);
        return modelMapper.map(entity, Rol.class);
    }

    @Override
    public void delete(String nombre) {
        RolEntity entity = repository.findByNombre(nombre)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro un rol con ese nombre"));

        if(!entity.getActivo()) {
            throw new ConflictiveStateException("El rol ya esta desactivado");
        }

        entity.setActivo(false);
        repository.save(entity);
    }

    @Override
    public Optional<RolEntity> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    @Override
    public Optional<RolEntity> findEntityById(Long id) {
        return repository.findById(id);
    }
}
