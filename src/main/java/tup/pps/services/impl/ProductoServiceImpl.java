package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tup.pps.dtos.ProductoDTO;
import tup.pps.entities.CategoriaEntity;
import tup.pps.entities.MarcaEntity;
import tup.pps.entities.ProductoEntity;
import tup.pps.entities.ProductoXCategoriaEntity;
import tup.pps.exceptions.ConflictiveStateException;
import tup.pps.exceptions.EntryNotFoundException;
import tup.pps.models.Categoria;
import tup.pps.models.Marca;
import tup.pps.models.Producto;
import tup.pps.repositories.ProductoRepository;
import tup.pps.repositories.ProductoXCategoriaRepository;
import tup.pps.repositories.specs.ProductoSpecification;
import tup.pps.services.CategoriaService;
import tup.pps.services.MarcaService;
import tup.pps.services.ProductoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private ProductoXCategoriaRepository productoXCategoriaRepository;

    @Autowired
    private ProductoSpecification specification;

    @Autowired
    private ModelMapper modelMapper;

    // Servicios de tablas soporte
    @Autowired
    private MarcaService marcaService;

    @Autowired
    private CategoriaService categoriaService;

    @Override
    public void delete(Long id) {
        ProductoEntity producto = repository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ningun producto con ese ID"));

        if (!producto.getActivo()) {
            throw new ConflictiveStateException("El producto ya esta desactivado");
        }

        producto.setActivo(false);
        repository.save(producto);
    }

    @Override
    public Producto findById(Long id) {
        ProductoEntity producto = repository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("No se encontro ningun producto con ese ID"));

        return devolverModelo(producto);
    }

    @Override
    public Page<Producto> findAll(
            Pageable pageable,
            String nombre,
            String nombreMarca,
            List<String> categorias,
            Double precioMin,
            Double precioMax,
            Integer stockMin,
            Integer stockMax,
            Boolean activo
    ) {
        // Combinar todas las specifications
        Specification<ProductoEntity> spec = specification.byNombre(nombre)
                .and(specification.byMarca(nombreMarca))
                .and(specification.byCategorias(categorias))
                .and(specification.byPrecioRango(precioMin, precioMax))
                .and(specification.byStockRango(stockMin, stockMax))
                .and(specification.byActivo(activo));

        Page<ProductoEntity> entityPage = repository.findAll(spec, pageable);

        // Mapear usando devolverModelo para cada producto
        return entityPage.map(this::devolverModelo);
    }

    public Producto update(Long id, ProductoDTO productoDTO) {
        // 1. Buscar producto existente
        ProductoEntity productoExistente = repository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Producto no encontrado"));

        // 2. Resolver marca (igual = mantener, diferente = resolver)
        MarcaEntity marca = resolverMarca(productoDTO.getMarca());

        // 3. Resolver categorías nuevas
        List<CategoriaEntity> categoriasNuevas = resolverCategorias(productoDTO.getCategorias());

        // 4. Actualizar campos del producto
        actualizarCamposProducto(productoExistente, productoDTO, marca);

        // 5. Gestionar relaciones categorías (desactivar viejas, activar/crear nuevas)
        gestionarRelacionesCategorias(productoExistente, categoriasNuevas);

        // 6. Retornar modelo actualizado
        return devolverModelo(productoExistente);
    }

    private void actualizarCamposProducto(ProductoEntity productoExistente, ProductoDTO productoDTO, MarcaEntity marca) {
        // Actualizar campos básicos
        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setComentarios(productoDTO.getComentarios());
        productoExistente.setFotoUrl(productoDTO.getFotoUrl());
        productoExistente.setStock(productoDTO.getStock());
        productoExistente.setPrecio(productoDTO.getPrecio());

        // Actualizar marca (puede ser la misma o nueva)
        productoExistente.setMarca(marca);

        // El activo NO se toca - eso es responsabilidad de delete()

        // Guardar los cambios
        repository.save(productoExistente);
    }

    private void gestionarRelacionesCategorias(ProductoEntity producto, List<CategoriaEntity> categoriasNuevas) {

        // 1. Obtenemos todas las relaciones que tiene ese producto con categorias
        List<ProductoXCategoriaEntity> relacionesActuales =
                productoXCategoriaRepository.findByProducto(producto);

        // 2. En base a todas las relaciones, comparamos cuales de la lista de categorias de
        // la actualizacion estan ahi, y si alguna de esas esta en false, la "apagamos"
        for (ProductoXCategoriaEntity relacion : relacionesActuales) {
            boolean categoriaEnDTO = categoriasNuevas.stream()
                    .anyMatch(cat -> cat.getId().equals(relacion.getCategoria().getId()));

            if (!categoriaEnDTO && relacion.getActivo()) {
                relacion.setActivo(false);
                productoXCategoriaRepository.save(relacion);
            }
        }

        // 3. Luego faltaria agregar las que no estan en las previas, pero salian como "apagadas",
        // simplemente les pones true, y para las caracteristicas que nunca se
        // relacionaron con el producto creamos la relacion.
        for (CategoriaEntity categoriaNueva : categoriasNuevas) {
            Optional<ProductoXCategoriaEntity> relacionExistente = relacionesActuales.stream()
                    .filter(rel -> rel.getCategoria().getId().equals(categoriaNueva.getId()))
                    .findFirst();

            if (relacionExistente.isPresent()) {
                // Existe pero esta activo = false, (REACTIVACION)
                if (!relacionExistente.get().getActivo()) {
                    relacionExistente.get().setActivo(true);
                    productoXCategoriaRepository.save(relacionExistente.get());
                }
            } else {
                // No existe relacion, asi que se crea una
                ProductoXCategoriaEntity nuevaRelacion = new ProductoXCategoriaEntity();
                nuevaRelacion.setProducto(producto);
                nuevaRelacion.setCategoria(categoriaNueva);
                nuevaRelacion.setActivo(true);
                productoXCategoriaRepository.save(nuevaRelacion);
            }
        }
    }

    @Override
    public Producto save(ProductoDTO productoDTO) {
        // 1. Resolver MARCA
        MarcaEntity marca = resolverMarca(productoDTO.getMarca());

        // 2. Resolver CATEGORÍAS
        List<CategoriaEntity> categorias = resolverCategorias(productoDTO.getCategorias());

        // 3. Crear PRODUCTO
        ProductoEntity producto = crearProducto(productoDTO, marca);

        // 4. Crear relaciones PRODUCTO_X_CATEGORIA
        crearRelacionesCategorias(producto, categorias);

        // 5. Retornar producto completo con categorías
        return devolverModelo(producto);
    }

    // Métodos auxiliares - implementaremos uno por uno
    private MarcaEntity resolverMarca(String nombreMarca) {
        Optional<MarcaEntity> optionalMarca = marcaService.findByNombre(nombreMarca);
        if(optionalMarca.isEmpty() || optionalMarca.get().getActivo().equals(false)) {
            return modelMapper.map(marcaService.save(nombreMarca), MarcaEntity.class);
        }
        return optionalMarca.get();
    }

    private List<CategoriaEntity> resolverCategorias(List<String> nombresCategorias) {
        List<CategoriaEntity> categorias = new ArrayList<>();

        for(String categoria : nombresCategorias) {
            Optional<CategoriaEntity> optionalCategoria = categoriaService.findByNombre(categoria);
            if(optionalCategoria.isEmpty() || !optionalCategoria.get().getActivo()) {
                categorias.add(modelMapper.map(categoriaService.save(categoria), CategoriaEntity.class));
            } else {
                categorias.add(optionalCategoria.get());
            }
        }
        return categorias;
    }

    private ProductoEntity crearProducto(ProductoDTO dto, MarcaEntity marca) {
        // Aca normalmente hacemos alguna validacion por campo unique,
        // pero como los productos no son unicos, y pueden tener el mismo nombre, misma marca
        // y aun asi ser diferentes, nos salteamos esta etapa en especifico
        // porque no tendria sentido en este contexto.

        ProductoEntity entity = new ProductoEntity();
        entity.setNombre(dto.getNombre());
        entity.setComentarios(dto.getComentarios());
        entity.setFotoUrl(dto.getFotoUrl());
        entity.setMarca(marca);
        entity.setStock(dto.getStock());
        entity.setPrecio(dto.getPrecio());
        entity.setActivo(true);

        return repository.save(entity);
    }

    private void crearRelacionesCategorias(ProductoEntity producto, List<CategoriaEntity> categorias) {
        for(CategoriaEntity categoria : categorias) {
            // Buscar si ya existe la relación
            List<ProductoXCategoriaEntity> relacionesExistentes =
                    productoXCategoriaRepository.findByProductoAndCategoria(producto, categoria);

            if (!relacionesExistentes.isEmpty()) {
                // Reactivar la primera relación encontrada (debería ser única)
                ProductoXCategoriaEntity relacionExistente = relacionesExistentes.get(0);
                relacionExistente.setActivo(true);
                productoXCategoriaRepository.save(relacionExistente);
            } else {
                // Crear nueva relación
                ProductoXCategoriaEntity nuevaRelacion = new ProductoXCategoriaEntity();
                nuevaRelacion.setProducto(producto);
                nuevaRelacion.setCategoria(categoria);
                nuevaRelacion.setActivo(true);
                productoXCategoriaRepository.save(nuevaRelacion);
            }
        }
    }

    private Producto devolverModelo(ProductoEntity producto) {
        Producto modelo = new Producto();
        modelo.setId(producto.getId());
        modelo.setNombre(producto.getNombre());
        modelo.setComentarios(producto.getComentarios());
        modelo.setFotoUrl(producto.getFotoUrl());
        modelo.setMarca(modelMapper.map(producto.getMarca(), Marca.class));
        modelo.setStock(producto.getStock());
        modelo.setPrecio(producto.getPrecio());
        modelo.setActivo(producto.getActivo());

        List<Categoria> listaModels = productoXCategoriaRepository
                .findByProductoAndActivoIsTrue(producto)
                .stream()
                .map(relacion -> modelMapper.map(relacion.getCategoria(), Categoria.class))
                .toList();

        modelo.setCategorias(listaModels);
        return modelo;
    }

    @Override
    public Optional<ProductoEntity> findEntityById(Long id) {
        return repository.findById(id);
    }
}
