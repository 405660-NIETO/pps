package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.dtos.usuarios.UsuarioRegistroDTO;
import tup.pps.dtos.usuarios.UsuarioUpdateDTO;
import tup.pps.entities.RolEntity;
import tup.pps.entities.UsuarioEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.exceptions.ResourceAlreadyExistsException;
import tup.pps.models.Usuario;
import tup.pps.repositories.UsuarioRepository;
import tup.pps.repositories.specs.UsuarioSpecification;
import tup.pps.services.RolService;
import tup.pps.services.UsuarioService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioSpecification specification;

    @Autowired
    private RolService rolService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<UsuarioEntity> findEntityById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Usuario save(UsuarioRegistroDTO dto) {
        // 1. RESOLVER rol (validar que existe)
        RolEntity rol = resolverRol(dto.getRolId());

        // 2. VERIFICAR email existente
        Optional<UsuarioEntity> usuarioExistente = repository.findByEmail(dto.getEmail());

        if (usuarioExistente.isPresent()) {
            if (usuarioExistente.get().getActivo()) {
                throw new ResourceAlreadyExistsException("Ya existe un usuario con ese email");
            } else {
                // REACTIVAR usuario inactivo
                return reactivarUsuario(usuarioExistente.get(), dto, rol);
            }
        }

        // 3. CREAR nuevo usuario
        return crearUsuario(dto, rol);
    }

    @Override
    public Usuario update(Long id, UsuarioUpdateDTO dto) {
        // 1. BUSCAR usuario existente
        UsuarioEntity usuario = repository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Usuario no encontrado"));

        // 2. ACTUALIZAR campos básicos (siempre)
        actualizarPerfil(usuario, dto);

        // 3. GESTIONAR password (solo si viene en DTO)
        if (vienePasswordEnDTO(dto)) {
            actualizarPassword(usuario, dto);
        }

        // 4. GUARDAR y retornar
        UsuarioEntity saved = repository.save(usuario);
        return modelMapper.map(saved, Usuario.class);
    }

    @Override
    public Usuario findById(Long id) {
        return repository.findById(id)
                .map(entity -> modelMapper.map(entity, Usuario.class))
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ningun usuario con ese ID"));
    }

    @Override
    public UsuarioEntity findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ningun usuario con ese email"));
    }

    @Override
    public Page<Usuario> findAll(
            Pageable pageable,
            String email,
            String nombre,
            Long rolId,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Boolean activo
    ) {
        // Combinar todas las specifications - Pattern exacto de ProductoService
        Specification<UsuarioEntity> spec = specification.byEmail(email)
                .and(specification.byNombreApellido(nombre))
                .and(specification.byRol(rolId))
                .and(specification.byFechaRango(fechaDesde, fechaHasta))
                .and(specification.byActivo(activo));

        Page<UsuarioEntity> entityPage = repository.findAll(spec, pageable);

        // Mapear directo - No necesita devolverModelo como Productos
        return entityPage.map(entity -> modelMapper.map(entity, Usuario.class));
    }

    @Override
    public void delete(Long id) {
        // 1. BUSCAR por ID
        UsuarioEntity usuario = repository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ningun usuario con ese ID"));

        // 2. VALIDAR que esté activo
        if (!usuario.getActivo()) {
            throw new ConflictiveStateException("El usuario ya esta desactivado");
        }

        // 3. DESACTIVAR (borrado lógico)
        usuario.setActivo(false);
        repository.save(usuario);
    }

    @Override
    public void actualizarUsuario(UsuarioEntity usuario) {
        repository.save(usuario);
    }

    // Metodos para Save

    private RolEntity resolverRol(Long rolId) {
        return rolService.findEntityById(rolId)
                .orElseThrow(() -> new EntryNotFoundException("Rol no encontrado"));
    }

    private Usuario reactivarUsuario(UsuarioEntity usuario, UsuarioRegistroDTO dto, RolEntity rol) {
        usuario.setActivo(true);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setPassword(dto.getPassword()); // Plain text por ahora
        usuario.setRol(rol);
        usuario.setFechaCreacion(usuario.getFechaCreacion());

        UsuarioEntity saved = repository.save(usuario);
        return modelMapper.map(saved, Usuario.class);
    }

    private Usuario crearUsuario(UsuarioRegistroDTO dto, RolEntity rol) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword()); // Plain text
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setRol(rol);
        entity.setFechaCreacion(LocalDateTime.now());
        entity.setActivo(true);

        UsuarioEntity saved = repository.save(entity);
        return modelMapper.map(saved, Usuario.class);
    }

    // Metodos para Update
    private void actualizarPerfil(UsuarioEntity usuario, UsuarioUpdateDTO dto) {
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
    }

    private boolean vienePasswordEnDTO(UsuarioUpdateDTO dto) {
        return dto.getPasswordActual() != null && !dto.getPasswordActual().isBlank() &&
                dto.getPasswordNueva() != null && !dto.getPasswordNueva().isBlank();
    }

    private void actualizarPassword(UsuarioEntity usuario, UsuarioUpdateDTO dto) {
        // Validar password actual
        if (!usuario.getPassword().equals(dto.getPasswordActual())) {
            throw new ConflictiveStateException("Password actual incorrecta");
        }

        // Actualizar a nueva password
        usuario.setPassword(dto.getPasswordNueva());
    }
}