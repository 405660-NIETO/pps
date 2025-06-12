package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.dtos.DetalleFacturaDTO;
import tup.pps.dtos.FacturaDTO;
import tup.pps.entities.*;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.models.*;
import tup.pps.repositories.FacturaRepository;
import tup.pps.repositories.specs.FacturaSpecification;
import tup.pps.services.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaRepository repository;

    @Autowired
    private FacturaSpecification specification;

    @Autowired
    private ReparacionService reparacionService;

    @Autowired
    private DetalleFacturaService detalleFacturaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FormaPagoService formaPagoService;

    @Autowired
    private SedeService sedeService;

    @Autowired
    private ModelMapper modelMapper;

    public Optional<FacturaEntity> findEntityById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<Factura> findAll(
            Pageable pageable,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            Long usuarioId,
            Long sedeId,
            Long formaPagoId,
            Double montoMin,
            Double montoMax,
            Boolean tieneReparaciones,
            Boolean tieneProductos,
            Integer cantidadItemsMin,
            Boolean activo
    ) {
        Specification<FacturaEntity> spec = specification.byFechaRango(fechaDesde, fechaHasta)
                .and(specification.byUsuario(usuarioId))
                .and(specification.bySede(sedeId))
                .and(specification.byFormaPago(formaPagoId))
                .and(specification.byMontoRango(montoMin, montoMax))
                .and(specification.byTieneReparaciones(tieneReparaciones))
                .and(specification.byTieneProductos(tieneProductos))
                .and(specification.byCantidadItemsMin(cantidadItemsMin))
                .and(specification.byActivo(activo));

        Page<FacturaEntity> entityPage = repository.findAll(spec, pageable);

        // Mapear usando devolverModelo para cada factura
        return entityPage.map(this::devolverModelo);
    }

    @Override
    public Factura save(FacturaDTO facturaDTO) {
        // 1. Crear factura base
        FacturaEntity factura = crearFactura(facturaDTO);

        // 2. Placeholder Mercado Pago
        if (esMercadoPago(facturaDTO.getFormaPagoId())) {
            // TODO: Magia de Mercado Pago aquí
        }

        // 3. Conectar las tuberías
        asociarReparaciones(factura, facturaDTO.getReparacionIds());
        crearDetallesFactura(factura, facturaDTO.getProductos());

        return devolverModelo(factura);
    }

    private boolean esMercadoPago(Long formaPagoId) {
        return formaPagoId.equals(7L);  // ID de Mercado Pago en data.sql
    }

    private void asociarReparaciones(FacturaEntity factura, List<Long> reparacionIds) {
        if (reparacionIds == null || reparacionIds.isEmpty()) return;

        for (Long reparacionId : reparacionIds) {
            // 1. Buscar reparación existente (usando el servicio)
            ReparacionEntity reparacion = reparacionService.findEntityById(reparacionId)
                    .orElseThrow(() -> new EntryNotFoundException("Reparacion no encontrada"));

            // 2. Validar que no esté ya facturada
            if (reparacion.getFactura() != null) {
                throw new ConflictiveStateException("Reparacion ya facturada");
            }

            // 3. "Sellar" la reparación con la factura
            reparacion.setFactura(factura);

            // 4. Establecer fecha de entrega = fecha de factura (lógica de negocio)
            reparacion.setFechaEntrega(factura.getFecha());

            // 5. Actualizar usando el servicio (patrón limpio)
            reparacionService.actualizarReparacion(reparacion);
        }
    }

    private void crearDetallesFactura(FacturaEntity factura, List<DetalleFacturaDTO> detalles) {
        if (detalles == null || detalles.isEmpty()) return;

        detalles.forEach(dto -> detalleFacturaService.save(dto, factura));
    }

    private FacturaEntity crearFactura(FacturaDTO facturaDTO) {
        // 1. RESOLVER entidades de soporte (validar que existen)
        UsuarioEntity usuario = resolverUsuario(facturaDTO.getUsuarioId());
        SedeEntity sede = resolverSede(facturaDTO.getSedeId());
        FormaPagoEntity formaPago = resolverFormaPago(facturaDTO.getFormaPagoId());

        // 2. CREAR la entidad factura
        FacturaEntity factura = new FacturaEntity();

        // 3. DATOS AUTOMÁTICOS
        factura.setFecha(LocalDateTime.now());  // Timestamp actual

        // 4. REFERENCIAS a entidades de soporte
        factura.setUsuario(usuario);
        factura.setSede(sede);
        factura.setFormaPago(formaPago);

        // 5. DATOS DEL CLIENTE (todos opcionales)
        factura.setClienteNombre(facturaDTO.getClienteNombre());
        factura.setClienteApellido(facturaDTO.getClienteApellido());
        factura.setClienteDocumento(facturaDTO.getClienteDocumento());
        factura.setClienteTelefono(facturaDTO.getClienteTelefono());
        factura.setClienteCelular(facturaDTO.getClienteCelular());
        factura.setClienteEmail(facturaDTO.getClienteEmail());

        // 6. GUARDAR y obtener ID generado
        return repository.save(factura);
    }

    // Métodos de resolución necesarios
    private UsuarioEntity resolverUsuario(Long usuarioId) {
        return usuarioService.findEntityById(usuarioId)
                .orElseThrow(() -> new EntryNotFoundException("Usuario no encontrado"));
    }

    private SedeEntity resolverSede(Long sedeId) {
        return sedeService.findEntityById(sedeId)
                .orElseThrow(() -> new EntryNotFoundException("Sede no encontrada"));
    }

    private FormaPagoEntity resolverFormaPago(Long formaPagoId) {
        return formaPagoService.findEntityById(formaPagoId)
                .orElseThrow(() -> new EntryNotFoundException("Forma de pago no encontrada"));
    }

    private Factura devolverModelo(FacturaEntity facturaEntity) {
        Factura modelo = new Factura();

        // 1. DATOS BÁSICOS de la factura
        modelo.setId(facturaEntity.getId());
        modelo.setFecha(facturaEntity.getFecha());

        // 2. ENTIDADES DE SOPORTE (ya vienen cargadas por JPA)
        modelo.setUsuario(modelMapper.map(facturaEntity.getUsuario(), Usuario.class));
        modelo.setSede(modelMapper.map(facturaEntity.getSede(), Sede.class));
        modelo.setFormaPago(modelMapper.map(facturaEntity.getFormaPago(), FormaPago.class));

        // 3. DATOS DEL CLIENTE
        modelo.setClienteNombre(facturaEntity.getClienteNombre());
        modelo.setClienteApellido(facturaEntity.getClienteApellido());
        modelo.setClienteDocumento(facturaEntity.getClienteDocumento());
        modelo.setClienteTelefono(facturaEntity.getClienteTelefono());
        modelo.setClienteCelular(facturaEntity.getClienteCelular());
        modelo.setClienteEmail(facturaEntity.getClienteEmail());

        // 4. BUSCAR DETALLES relacionados (aquí brilla el findAll sin paginado)
        List<DetalleFactura> detalles = buscarDetallesPorFactura(facturaEntity.getId());
        modelo.setDetalles(detalles);

        // 5. BUSCAR REPARACIONES relacionadas
        List<Reparacion> reparaciones = buscarReparacionesPorFactura(facturaEntity.getId());
        modelo.setReparaciones(reparaciones);

        return modelo;
    }

    private List<DetalleFactura> buscarDetallesPorFactura(Long facturaId) {
        return detalleFacturaService.findByFacturaId(facturaId);
    }

    private List<Reparacion> buscarReparacionesPorFactura(Long facturaId) {
        return reparacionService.findByFacturaId(facturaId);
    }
}
