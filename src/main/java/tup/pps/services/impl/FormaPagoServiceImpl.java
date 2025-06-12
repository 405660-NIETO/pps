package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.entities.FormaPagoEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.ResourceAlreadyExistsException;
import tup.pps.models.FormaPago;
import tup.pps.repositories.FormaPagoRepository;
import tup.pps.repositories.specs.FormaPagoSpecification;
import tup.pps.services.FormaPagoService;

import java.util.List;
import java.util.Optional;

/**
 * Tabla Soporte - Patrón B
 * Bajo volumen, sin paginado, con update
 */
@Service
public class FormaPagoServiceImpl implements FormaPagoService {

    @Autowired
    private FormaPagoRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FormaPagoSpecification specification;

    @Override
    public List<FormaPago> findAll(String nombre, Boolean activo) {
        Specification<FormaPagoEntity> spec = specification.byNombre(nombre)
                .and(specification.byActivo(activo));

        List<FormaPagoEntity> entities = repository.findAll(spec);

        return entities.stream()
                .map(entity -> modelMapper.map(entity, FormaPago.class))
                .toList();
    }

    @Override
    public FormaPago findById(Long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, FormaPago.class))
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ninguna forma de pago con ese ID"));
    }

    @Override
    public FormaPago update(String nombreActual, String nombreNuevo) {
        // 1. Verificar que existe el registro a actualizar
        FormaPagoEntity entity = repository.findByNombre(nombreActual)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro una forma de pago con ese nombre"));

        // 2. Verificar que no esté intentando cambiar al mismo nombre
        if (nombreActual.equals(nombreNuevo)) {
            throw new ConflictiveStateException("El nuevo nombre debe ser diferente al actual");
        }

        // 3. Verificar que el nuevo nombre no exista ya
        Optional<FormaPagoEntity> existeNuevoNombre = repository.findByNombre(nombreNuevo);
        if (existeNuevoNombre.isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe una forma de pago con ese nombre");
        }

        // 4. Actualizar y guardar
        entity.setNombre(nombreNuevo);
        entity = repository.save(entity);

        return modelMapper.map(entity, FormaPago.class);
    }

    @Override
    public FormaPago save(String formaPago) {
        Optional<FormaPagoEntity> formaPagoOpcional = repository.findByNombre(formaPago);
        if(formaPagoOpcional.isPresent()) {
            if(formaPagoOpcional.get().getActivo()) {
                throw new ResourceAlreadyExistsException("Ya hay una forma de pago con ese nombre");
            }
            else {
                formaPagoOpcional.get().setActivo(true);
                FormaPagoEntity entity = repository.save(formaPagoOpcional.get());
                return modelMapper.map(entity, FormaPago.class);
            }
        }

        FormaPagoEntity entity = new FormaPagoEntity();
        entity.setNombre(formaPago);
        entity.setActivo(true);
        entity = repository.save(entity);
        return modelMapper.map(entity, FormaPago.class);
    }

    @Override
    public void delete(String nombre) {
        FormaPagoEntity entity = repository.findByNombre(nombre)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro una forma de pago con ese nombre"));

        if(!entity.getActivo()) {
            throw new ConflictiveStateException("La forma de pago ya esta desactivada");
        }

        entity.setActivo(false);
        repository.save(entity);
    }

    @Override
    public Optional<FormaPagoEntity> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    @Override
    public Optional<FormaPagoEntity> findEntityById(Long id) {
        return repository.findById(id);
    }
}
