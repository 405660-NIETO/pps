# Patrones de Tablas Soporte

## Definición
Las **Tablas Soporte** son entidades de catálogo/referencia que sustentan la funcionalidad principal del sistema. Se clasifican en dos patrones según su volumen de datos y necesidades de actualización.

---

## Patrón A: Alto Volumen (Con Paginado)
**Características:**
- Gran volumen de registros esperado (100+ registros)
- Necesita paginación para performance
- **NO tiene UPDATE** (protege integridad histórica)
- Reutilización con borrado lógico

**Estructura técnica:**
- `Page<Entity> findAll(Pageable, String nombre, Boolean activo)`
- `findById()`, `save()`, `delete()`
- Specification con filtros combinables
- Repository extends `JpaSpecificationExecutor`

**Implementaciones:**
- **Categoría** - Clasificación de productos
- **Marca** - Fabricantes de instrumentos
- **Trabajo** - Tipos de reparaciones

---

## Patrón B: Bajo Volumen (Sin Paginado + Update)
**Características:**
- Bajo volumen de registros (5-10 máximo)
- **SÍ tiene UPDATE** (datos operativos que pueden cambiar)
- Lista completa sin paginación
- Specification para filtros flexibles

**Estructura técnica:**
- `List<Entity> findAll(String nombre, Boolean activo)`
- `findById()`, `save()`, `update()`, `delete()`
- Specification sin paginado
- Repository con `List<Entity> findAll(Specification<Entity>)`

**Implementaciones:**
- **FormaPago** - Formas de pago (Efectivo, Tarjeta, etc.)
- **Rol** - Roles de usuario (Admin, Empleado, Luthier)
- **Sede** - Sucursales del negocio (*Especial: direccion unique, no nombre*)

---

## Variante Especial: Sede (Patrón B con campo unique diferente)
**Particularidad:**
- Usa `direccion` como campo unique en lugar de `nombre`
- Permite múltiples sedes con mismo nombre pero diferentes direcciones
- Update puede modificar tanto nombre como dirección
- Delete y búsquedas por direccion

---

## Beneficios de esta Arquitectura
1. **Consistencia** - Patrones predecibles y reutilizables
2. **Performance** - Paginado solo donde es necesario
3. **Integridad** - Protección de datos históricos vs flexibilidad operativa
4. **Mantenibilidad** - Código estructurado y documentado
5. **Escalabilidad** - Fácil agregar nuevas tablas siguiendo patrones establecidos
