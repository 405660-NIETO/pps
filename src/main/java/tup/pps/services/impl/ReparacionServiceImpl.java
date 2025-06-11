package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.dtos.ReparacionDTO;
import tup.pps.entities.ReparacionEntity;
import tup.pps.entities.ReparacionXTrabajoEntity;
import tup.pps.entities.TrabajoEntity;
import tup.pps.entities.UsuarioEntity;
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
