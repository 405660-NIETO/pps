package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.dtos.ReparacionDTO;
import tup.pps.entities.ReparacionEntity;
import tup.pps.entities.ReparacionXTrabajoEntity;
import tup.pps.entities.TrabajoEntity;
import tup.pps.entities.UsuarioEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.models.Reparacion;
import tup.pps.models.Trabajo;
import tup.pps.models.Usuario;
import tup.pps.repositories.ReparacionRepository;
import tup.pps.repositories.ReparacionXTrabajoRepository;
import tup.pps.repositories.specs.ReparacionSpecification;
import tup.pps.services.ReparacionService;
import tup.pps.services.TrabajoService;
import tup.pps.services.UsuarioService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReparacionServiceImpl implements ReparacionService {

    @Autowired
    private ReparacionRepository repository;

    @Autowired
    private ReparacionXTrabajoRepository reparacionXTrabajoRepository;

    @Autowired
    private ReparacionSpecification specification;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TrabajoService trabajoService;

    @Override
    public Page<Reparacion> findAll(
            Pageable pageable,
            String usuario,
            List<String> trabajos,
            LocalDateTime fechaInicio,
            LocalDateTime fechaEntrega,
            Double precioMin,
            Double precioMax,
            Boolean activo
    ) {
        // Combinar todas las specifications
        Specification<ReparacionEntity> spec = specification.byUsuario(usuario)
                .and(specification.byTrabajos(trabajos))
                .and(specification.byFechaRango(fechaInicio, fechaEntrega))
                .and(specification.byPrecioRango(precioMin, precioMax))
                .and(specification.byActivo(activo));

        Page<ReparacionEntity> entityPage = repository.findAll(spec, pageable);

        // Mapear usando devolverModelo para cada reparación
        return entityPage.map(this::devolverModelo);
    }

    @Override
    public Reparacion update(Long id, ReparacionDTO reparacionDTO) {
        // 1. Buscar reparación existente
        ReparacionEntity reparacionExistente = repository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Reparacion no encontrada"));

        // 2. VALIDAR que no sea un registro histórico (ya entregado)
        if (reparacionExistente.getFechaEntrega() != null &&
                reparacionExistente.getFechaEntrega().isBefore(LocalDateTime.now())) {
            throw new ConflictiveStateException("No se puede modificar una reparacion ya entregada");
        }

        // 3. Resolver usuario (puede cambiar de luthier)
        UsuarioEntity usuario = resolverUsuario(reparacionDTO.getUsuarioId());

        // 4. Resolver trabajos nuevos
        List<TrabajoEntity> trabajosNuevos = resolverTrabajos(reparacionDTO.getTrabajos());

        // 5. Actualizar campos de la reparación
        actualizarCamposReparacion(reparacionExistente, reparacionDTO, usuario);

        // 6. Gestionar relaciones trabajos (desactivar viejos, activar/crear nuevos)
        gestionarRelacionesTrabajos(reparacionExistente, trabajosNuevos);

        // 7. Retornar modelo actualizado
        return devolverModelo(reparacionExistente);
    }

    private void actualizarCamposReparacion(ReparacionEntity reparacionExistente, ReparacionDTO reparacionDTO, UsuarioEntity usuario) {
        // Actualizar campos básicos
        reparacionExistente.setUsuario(usuario);  // Puede cambiar de luthier
        reparacionExistente.setDetalles(reparacionDTO.getDetalles());
        reparacionExistente.setFechaInicio(reparacionDTO.getFechaInicio());
        reparacionExistente.setFechaEntrega(reparacionDTO.getFechaEntrega());
        reparacionExistente.setPrecio(reparacionDTO.getPrecio());

        // El activo NO se toca - eso es responsabilidad de delete()
        // factura tampoco se toca aquí - eso será responsabilidad de facturación

        // Guardar los cambios
        repository.save(reparacionExistente);
    }

    private void gestionarRelacionesTrabajos(ReparacionEntity reparacion, List<TrabajoEntity> trabajosNuevos) {

        // 1. Obtener TODAS las relaciones actuales (activas + inactivas)
        List<ReparacionXTrabajoEntity> relacionesActuales =
                reparacionXTrabajoRepository.findByReparacion(reparacion);

        // 2. Desactivar trabajos que YA NO están en el DTO
        for (ReparacionXTrabajoEntity relacion : relacionesActuales) {
            boolean trabajoEnDTO = trabajosNuevos.stream()
                    .anyMatch(trabajo -> trabajo.getId().equals(relacion.getTrabajo().getId()));

            if (!trabajoEnDTO && relacion.getActivo()) {
                relacion.setActivo(false);
                reparacionXTrabajoRepository.save(relacion);
            }
        }

        // 3. Activar/crear trabajos que SÍ están en el DTO
        for (TrabajoEntity trabajoNuevo : trabajosNuevos) {
            List<ReparacionXTrabajoEntity> relacionExistente = relacionesActuales.stream()
                    .filter(rel -> rel.getTrabajo().getId().equals(trabajoNuevo.getId()))
                    .toList();

            if (!relacionExistente.isEmpty()) {
                // Reactivar si existe pero está inactiva
                ReparacionXTrabajoEntity relacion = relacionExistente.get(0);
                if (!relacion.getActivo()) {
                    relacion.setActivo(true);
                    reparacionXTrabajoRepository.save(relacion);
                }
            } else {
                // Crear nueva relación
                ReparacionXTrabajoEntity nuevaRelacion = new ReparacionXTrabajoEntity();
                nuevaRelacion.setReparacion(reparacion);
                nuevaRelacion.setTrabajo(trabajoNuevo);
                nuevaRelacion.setActivo(true);
                reparacionXTrabajoRepository.save(nuevaRelacion);
            }
        }
    }

    public Reparacion save(ReparacionDTO reparacionDTO) {
        // 1. Resolver USUARIO
        UsuarioEntity usuario = resolverUsuario(reparacionDTO.getUsuarioId());

        // 2. Resolver TRABAJOS (igual que categorías)
        List<TrabajoEntity> trabajos = resolverTrabajos(reparacionDTO.getTrabajos());

        // 3. Crear REPARACIÓN
        ReparacionEntity reparacion = crearReparacion(reparacionDTO, usuario);

        // 4. Crear relaciones REPARACION_X_TRABAJO
        crearRelacionesTrabajos(reparacion, trabajos);

        // 5. Retornar modelo completo
        return devolverModelo(reparacion);
    }

    private UsuarioEntity resolverUsuario(Long id) {
        return usuarioService.findEntityById(id).orElseThrow(() -> new EntryNotFoundException("Usuario no encontrado"));
    }

    private List<TrabajoEntity> resolverTrabajos(List<String> nombresTrabajos) {
        List<TrabajoEntity> trabajos = new ArrayList<>();

        for(String trabajo : nombresTrabajos) {
            Optional<TrabajoEntity> optionalTrabajo = trabajoService.findByNombre(trabajo);
            if(optionalTrabajo.isEmpty() || !optionalTrabajo.get().getActivo()) {
                trabajos.add(modelMapper.map(trabajoService.save(trabajo), TrabajoEntity.class));
            } else {
                trabajos.add(optionalTrabajo.get());
            }
        }
        return trabajos;
    }

    private ReparacionEntity crearReparacion(ReparacionDTO dto, UsuarioEntity usuario) {
        ReparacionEntity entity = new ReparacionEntity();
        entity.setUsuario(usuario);
        entity.setDetalles(dto.getDetalles());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaEntrega(dto.getFechaEntrega());
        entity.setPrecio(dto.getPrecio());
        entity.setActivo(true);
        entity.setFactura(null);  // Por ahora null

        return repository.save(entity);
    }

    private void crearRelacionesTrabajos(ReparacionEntity reparacion, List<TrabajoEntity> trabajos) {
        for(TrabajoEntity trabajo : trabajos) {
            // Buscar si ya existe la relación
            List<ReparacionXTrabajoEntity> relacionesExistentes =
                    reparacionXTrabajoRepository.findByReparacionAndTrabajo(reparacion, trabajo);

            if (!relacionesExistentes.isEmpty()) {
                // Reactivar la primera relación encontrada (debería ser única)
                ReparacionXTrabajoEntity relacionExistente = relacionesExistentes.get(0);
                relacionExistente.setActivo(true);
                reparacionXTrabajoRepository.save(relacionExistente);
            } else {
                // Crear nueva relación
                ReparacionXTrabajoEntity nuevaRelacion = new ReparacionXTrabajoEntity();
                nuevaRelacion.setReparacion(reparacion);
                nuevaRelacion.setTrabajo(trabajo);
                nuevaRelacion.setActivo(true);
                reparacionXTrabajoRepository.save(nuevaRelacion);
            }
        }
    }

    private Reparacion devolverModelo(ReparacionEntity reparacion) {
        Reparacion modelo = new Reparacion();
        modelo.setId(reparacion.getId());
        modelo.setUsuario(modelMapper.map(reparacion.getUsuario(), Usuario.class));
        modelo.setDetalles(reparacion.getDetalles());
        modelo.setFechaInicio(reparacion.getFechaInicio());
        modelo.setFechaEntrega(reparacion.getFechaEntrega());
        modelo.setPrecio(reparacion.getPrecio());
        modelo.setActivo(reparacion.getActivo());

        // Obtener trabajos activos (igual que categorías en Productos)
        List<Trabajo> trabajos = reparacionXTrabajoRepository
                .findByReparacionAndActivoIsTrue(reparacion)
                .stream()
                .map(relacion -> modelMapper.map(relacion.getTrabajo(), Trabajo.class))
                .toList();

        modelo.setTrabajos(trabajos);
        return modelo;
    }
}
