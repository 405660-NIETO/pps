package tup.pps.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tup.pps.dtos.ProductoDTO;
import tup.pps.entities.MarcaEntity;
import tup.pps.entities.ProductoEntity;
import tup.pps.models.Categoria;
import tup.pps.models.Marca;
import tup.pps.models.Producto;
import tup.pps.repositories.ProductoRepository;
import tup.pps.repositories.ProductoXCategoriaRepository;
import tup.pps.services.CategoriaService;
import tup.pps.services.MarcaService;
import tup.pps.services.ProductoService;

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
        List<Categoria> categorias = resolverCategorias(productoDTO.getCategorias());

        // 3. Crear PRODUCTO
        ProductoEntity producto = crearProducto(productoDTO, marca);

        // 4. Crear relaciones PRODUCTO_X_CATEGORIA
        crearRelacionesCategorias(producto, categorias);

        // 5. Retornar producto completo con categorías
        return mapearConCategorias(producto, categorias);
    }

    // Métodos auxiliares - implementaremos uno por uno
    private MarcaEntity resolverMarca(String nombreMarca) {
        Optional<MarcaEntity> optionalMarca = marcaService.findByNombre(nombreMarca);
        if(optionalMarca.isEmpty() || optionalMarca.get().getActivo().equals(false)) {
            return modelMapper.map(marcaService.save(nombreMarca), MarcaEntity.class);
        }
        return optionalMarca.get();
    }

    private List<Categoria> resolverCategorias(List<String> nombresCategorias) {
        // TODO: Implementar
        return null;
    }

    private ProductoEntity crearProducto(ProductoDTO dto, MarcaEntity marca) {
        // TODO: Implementar
        return null;
    }

    private void crearRelacionesCategorias(ProductoEntity producto, List<Categoria> categorias) {
        // TODO: Implementar
    }

    private Producto mapearConCategorias(ProductoEntity producto, List<Categoria> categorias) {
        // TODO: Implementar
        return null;
    }
}
