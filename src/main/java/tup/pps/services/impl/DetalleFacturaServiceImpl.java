package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.dtos.DetalleFacturaDTO;
import tup.pps.entities.DetalleFacturaEntity;
import tup.pps.entities.FacturaEntity;
import tup.pps.entities.ProductoEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.models.DetalleFactura;
import tup.pps.repositories.DetalleFacturaRepository;
import tup.pps.services.DetalleFacturaService;
import tup.pps.services.ProductoService;

@Service
public class DetalleFacturaServiceImpl implements DetalleFacturaService {

    @Autowired
    private DetalleFacturaRepository repository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ModelMapper modelMapper;

    // DetalleFacturaServiceImpl - estructura base
    @Override
    public DetalleFactura save(DetalleFacturaDTO dto, FacturaEntity factura) {
        // 1. Resolver producto
        ProductoEntity producto = resolverProducto(dto.getProductoId());

        // 2. Crear detalle
        DetalleFacturaEntity detalle = crearDetalle(dto, producto, factura);

        // 3. Mapear y retornar (simple, sin devolverModelo por ahora)
        return modelMapper.map(detalle, DetalleFactura.class);
    }

    // 1. Resolver Producto - Simple y directo
    private ProductoEntity resolverProducto(Long productoId) {
        return productoService.findEntityById(productoId)
                .orElseThrow(() -> new EntryNotFoundException("Producto no encontrado"));
    }


    // 3. Crear Detalle - Construcción paso a paso
    private DetalleFacturaEntity crearDetalle(DetalleFacturaDTO dto, ProductoEntity producto, FacturaEntity factura) {
        // Validar stock disponible
        if (dto.getCantidad() > producto.getStock()) {
            throw new ConflictiveStateException("Stock insuficiente. Disponible: "
                    + producto.getStock() + ", solicitado: " + dto.getCantidad());
        }

        DetalleFacturaEntity detalle = new DetalleFacturaEntity();
        detalle.setFactura(factura);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.getCantidad());

        // Precio: usar el del DTO si viene, sino el del producto
        Double precio = dto.getPrecio() != null ? dto.getPrecio() : producto.getPrecio();
        detalle.setPrecio(precio);

        // Guardar detalle
        DetalleFacturaEntity detalleGuardado = repository.save(detalle);

        // Actualizar stock del producto
        producto.setStock(producto.getStock() - dto.getCantidad());
        productoService.actualizarStock(producto);

        return detalleGuardado;
    }
}
