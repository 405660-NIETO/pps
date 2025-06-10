package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.dtos.ProductoDTO;
import tup.pps.entities.CategoriaEntity;
import tup.pps.entities.MarcaEntity;
import tup.pps.entities.ProductoEntity;
import tup.pps.entities.ProductoXCategoriaEntity;
import tup.pps.models.Categoria;
import tup.pps.models.Marca;
import tup.pps.models.Producto;
import tup.pps.repositories.ProductoRepository;
import tup.pps.repositories.ProductoXCategoriaRepository;
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
    private ModelMapper modelMapper;

    // Servicios de tablas soporte
    @Autowired
    private MarcaService marcaService;

    @Autowired
    private CategoriaService categoriaService;

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
}
