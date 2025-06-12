# Arquitectura Avanzada: Productos y Reparaciones

## Resumen Ejecutivo

Después de consolidar las **Tablas Soporte** (Patrón A y B), desarrollamos la arquitectura avanzada para **Productos** y **Reparaciones** - las entidades más complejas del sistema que manejan relaciones muchos-a-muchos con borrado lógico y creación automática de dependencias.

---

## Productos: La Entidad Maestra

### Características Principales
- **Relaciones complejas**: Muchos-a-muchos con Categorías, uno-a-muchos con Marca
- **Creación automática**: Si una categoría/marca no existe, se crea automáticamente
- **Borrado lógico**: En tabla intermedia `ProductoXCategoria` con campo `activo`
- **Sin UPDATE de nombre/marca**: Protege integridad histórica de facturas

### Arquitectura Implementada

#### 1. ProductoDTO
```java
public class ProductoDTO {
    private String nombre;
    private String comentarios;
    private String fotoUrl;
    private String marca;                // String simple (no objeto)
    private List<String> categorias;     // Lista de nombres (no objetos)
    private Integer stock;
    private Double precio;
}
```

#### 2. Patrón de Save() Modular
El método `save()` se divide en 5 métodos especializados:

```java
public Producto save(ProductoDTO productoDTO) {
    // 1. Resolver MARCA (buscar o crear/reactivar)
    MarcaEntity marca = resolverMarca(productoDTO.getMarca());
    
    // 2. Resolver CATEGORÍAS (buscar o crear/reactivar cada una)
    List<CategoriaEntity> categorias = resolverCategorias(productoDTO.getCategorias());
    
    // 3. Crear PRODUCTO
    ProductoEntity producto = crearProducto(productoDTO, marca);
    
    // 4. Crear relaciones PRODUCTO_X_CATEGORIA
    crearRelacionesCategorias(producto, categorias);
    
    // 5. Retornar producto completo con categorías
    return devolverModelo(producto);
}
```

#### 3. Update() Avanzado con Gestión de Relaciones
El update maneja:
- **Desactivación**: Categorías que ya no están en el DTO
- **Reactivación**: Categorías que estaban inactivas
- **Creación**: Nuevas categorías/relaciones

```java
private void gestionarRelacionesCategorias(ProductoEntity producto, List<CategoriaEntity> categoriasNuevas) {
    // 1. Obtener TODAS las relaciones (activas + inactivas)
    // 2. Desactivar las que ya no están en el DTO
    // 3. Activar/crear las que sí están en el DTO
}
```

#### 4. Specifications Complejas
Filtros avanzados con JOIN y subqueries:

```java
// Filtro por categorías con AND lógico (no OR)
public Specification<ProductoEntity> byCategorias(List<String> nombresCategorias) {
    // Usa COUNT() para verificar que tenga TODAS las categorías solicitadas
    return builder.equal(subquery, (long) nombresCategorias.size());
}
```

### Logros Técnicos
- ✅ **CRUD completo** funcional
- ✅ **Filtros avanzados** con paginación
- ✅ **Integridad referencial** mantenida
- ✅ **Performance optimizada** con queries eficientes

---

## Reparaciones: Aplicación del Patrón Maestro

### Evolución Arquitectónica
Reparaciones demostró la **escalabilidad del patrón** desarrollado en Productos. Lo que tomó días en Productos, se implementó en **minutos** en Reparaciones.

### Características Específicas
- **Relación con Usuario**: Uno-a-muchos (no crea usuarios automáticamente)
- **Relación con Trabajos**: Muchos-a-muchos (SÍ crea trabajos automáticamente)
- **Campos de fecha**: `fechaInicio` (obligatorio) y `fechaEntrega` (opcional)
- **Validación histórica**: No permite modificar reparaciones ya entregadas

### Implementación Clave

#### 1. ReparacionDTO
```java
public class ReparacionDTO {
    private Long usuarioId;           // ID del luthier (no crea usuarios)
    private String detalles;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaEntrega;
    private Double precio;
    private List<String> trabajos;    // Nombres de trabajos (crea automáticamente)
}
```

#### 2. Validación de Integridad Histórica
```java
// En update() - Protección de registros históricos
if (reparacionExistente.getFechaEntrega() != null && 
    reparacionExistente.getFechaEntrega().isBefore(LocalDateTime.now())) {
    throw new ConflictiveStateException("No se puede modificar una reparacion ya entregada");
}
```

### Reutilización de Patrones
- **resolverUsuario()**: Busca sin crear (diferente a resolverMarca)
- **resolverTrabajos()**: Idéntico a resolverCategorias
- **gestionarRelacionesTrabajos()**: Copy-paste de gestionarRelacionesCategorias
- **devolverModelo()**: Mismo patrón adaptado

---

## Arquitectura de Tabla Intermedia

### Problema Resuelto
Las tablas muchos-a-muchos tradicionales no permiten "quitar" relaciones sin perder historial. Nuestra solución:

```java
@Entity
@Table(name = "Productos_X_Categorias")
public class ProductoXCategoriaEntity {
    @Id
    private Long id;
    
    @ManyToOne
    private ProductoEntity producto;
    
    @ManyToOne  
    private CategoriaEntity categoria;
    
    @Column(nullable = false)
    private Boolean activo;  // ¡CLAVE!
}
```

### Beneficios
- ✅ **Historial completo**: Nunca se pierde información
- ✅ **Reactivación**: Relaciones pueden volver a activarse
- ✅ **Integridad**: Updates complejos sin pérdida de datos
- ✅ **Performance**: Queries eficientes filtrando por activo

---

## Specifications Avanzadas

### Filtro por Categorías con COUNT()
El mayor logro técnico fue resolver el filtro "tiene TODAS estas categorías":

```java
// PROBLEMA: IN() devuelve productos con UNA O MÁS categorías (OR lógico)
// SOLUCIÓN: COUNT() verifica que tenga TODAS las categorías (AND lógico)

subquery.select(builder.count(relacion.get("id")))
        .where(categoria.get("nombre").in(nombresCategorias));
        
return builder.equal(subquery, (long) nombresCategorias.size());
```

### Resultado
- **["Guitarras", "Eléctricas"]** → Solo productos que tengan AMBAS categorías
- **Performance**: Una sola query optimizada con subquery

---

## Metodología de Desarrollo

### Enfoque Modular
1. **DTO primero**: Definir interfaz de entrada limpia
2. **Métodos auxiliares**: Dividir complejidad en funciones específicas  
3. **Testing incremental**: Probar cada componente por separado
4. **Reutilización**: Aplicar patrones exitosos a nuevas entidades

### Principios Aplicados
- **Separación de responsabilidades**: Cada método tiene una función específica
- **Reutilización de código**: Patrones escalables y aplicables
- **Protección de datos**: Validaciones de negocio e integridad histórica
- **Performance first**: Queries optimizadas desde el diseño

---

## Impacto en el Proyecto

### Logros Arquitectónicos
- ✅ **75% del backend completado** con estas dos entidades
- ✅ **Patrón escalable** probado y funcional
- ✅ **Base sólida** para Facturación y DetalleFactura
- ✅ **Integridad de datos** garantizada

### Próximos Pasos Facilitados
- **Facturación**: Será "consumidor" de productos, no creador
- **DetalleFactura**: Aplicará el mismo patrón de relaciones
- **Spring Security**: Autenticación sobre base sólida
- **API Mercado Pago**: Integración en endpoints ya funcionales

---

## Conclusión

La implementación de **Productos** y **Reparaciones** estableció la arquitectura avanzada del sistema. El patrón desarrollado es:

- **Escalable**: Fácil aplicación a nuevas entidades
- **Robusto**: Maneja casos complejos sin errores  
- **Eficiente**: Performance optimizada
- **Mantenible**: Código limpio y bien estructurado

**El camino está pavimentado para el resto del desarrollo del e-commerce.**