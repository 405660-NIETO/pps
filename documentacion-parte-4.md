# 🎭 Parte 4: Facturación - La Orquestación Maestra

## Resumen Ejecutivo

Después de construir las bases sólidas (Tablas Soporte), dominar las relaciones complejas (Productos) y perfeccionar la integración avanzada (Reparaciones + DetalleFactura), llegamos al **momento culminante**: **FacturaService como director de orquesta**.

Esta no es solo otra entidad más - es el **corazón palpitante** del e-commerce, donde todos los patrones arquitectónicos convergen en una sinfonía perfecta de lógica de negocio.

---

## 🏗️ **Arquitectura de Facturación: El Paradigma Inmutable**

### **Principio Fundamental: Facturas = Documentos Legales**

Una vez creada, una factura se convierte en un **documento contable inmutable**. Esta decisión arquitectónica tiene profundas implicaciones:

```java
// ❌ NUNCA:
facturaService.update(facturaId, nuevosDatos);  // Ilegal contablemente

// ✅ SIEMPRE:
facturaService.delete(facturaId);               // Cancelar factura
facturaService.save(nuevaFacturaDTO);          // Crear nueva factura
```

### **Implicaciones del Paradigma Inmutable:**

| **Entidad** | **Update** | **Delete Individual** | **Razón** |
|---|---|---|---|
| **Factura** | ❌ | ✅ (borrado lógico) | Documento legal inmutable |
| **DetalleFactura** | ❌ | ❌ | Vinculado a factura inmutable |
| **Reparación** | ✅ | ✅ | Independiente hasta facturación |

---

## 🔄 **Reparaciones vs DetalleFactura: Dos Paradigmas Diferentes**

### **Reparación: Entidad Independiente con Ciclo de Vida Dinámico**

```java
// INDEPENDIENTE: Puede existir sin factura
Reparacion reparacion = reparacionService.save(dto);  // factura = null

// DINÁMICO: Puede actualizarse hasta su entrega
reparacionService.update(id, nuevoDatos);  // ✅ Permitido hasta fechaEntrega

// FLEXIBLE: Se "sella" al facturar
reparacionService.save(dto, facturaEntity);  // Se asigna a factura
```

**Ciclo de vida de Reparación:**
1. **Creación** → `facturaId = null` (instrumento en taller)
2. **Trabajo** → Updates permitidos (cambios de scope, luthier, etc.)
3. **Finalización** → `fechaEntrega` establecida
4. **Facturación** → Se asigna `facturaId` (inmutable desde aquí)

### **DetalleFactura: Entidad Transaccional Inmediata**

```java
// INMEDIATO: Solo existe como parte de una factura
DetalleFactura detalle = detalleFacturaService.save(dto, facturaEntity);

// INMUTABLE: No se puede modificar una vez creado
// detalleFacturaService.update();  // ❌ Método no existe
```

**Características clave:**
- ✅ **save()** - Crear detalle + actualizar stock automáticamente
- ✅ **findById()** - Para consultas específicas desde FacturaService
- ❌ **No update/delete** - Inmutable por naturaleza contable
- ❌ **No findAll** - Solo se consulta a través de su Factura padre

---

## 🎯 **Patrón de Sobrecarga en ReparacionService**

### **Problema: Flexibilidad sin Dependencias Circulares**

```java
// ❌ PROBLEMA: Dependencia circular
FacturaService → ReparacionService → FacturaService  // ¡MAL!

// ✅ SOLUCIÓN: Sobrecarga con inyección de dependencia
FacturaService → ReparacionService  // ¡BIEN!
```

### **Implementación de Sobrecarga:**

```java
@Service
public interface ReparacionService {
    // Para uso independiente (endpoints directos)
    Reparacion save(ReparacionDTO dto);
    
    // Para uso orquestado (desde FacturaService)
    Reparacion save(ReparacionDTO dto, FacturaEntity factura);
}

@Override
public Reparacion save(ReparacionDTO dto) {
    return save(dto, null);  // Delegación elegante
}

@Override
public Reparacion save(ReparacionDTO dto, FacturaEntity factura) {
    // Lógica completa aquí - factura puede ser null
}
```

### **Beneficios del Patrón:**
- ✅ **Sin dependencias circulares:** ReparacionService no conoce FacturaService
- ✅ **Máxima flexibilidad:** Funciona independiente O dentro de facturación
- ✅ **DRY aplicado:** Una sola implementación real
- ✅ **Backward compatibility:** Endpoints existentes intactos

---

## 📦 **Gestión Automática de Stock**

### **Flujo Integrado en DetalleFacturaService:**

```java
private DetalleFacturaEntity crearDetalle(DetalleFacturaDTO dto, ProductoEntity producto, FacturaEntity factura) {
    // 1. VALIDAR stock disponible
    if (dto.getCantidad() > producto.getStock()) {
        throw new ConflictiveStateException("Stock insuficiente");
    }
    
    // 2. CREAR detalle
    DetalleFacturaEntity detalle = new DetalleFacturaEntity();
    detalle.setPrecio(dto.getPrecio() != null ? dto.getPrecio() : producto.getPrecio());
    
    // 3. GUARDAR detalle
    DetalleFacturaEntity detalleGuardado = repository.save(detalle);
    
    // 4. ACTUALIZAR stock automáticamente
    producto.setStock(producto.getStock() - dto.getCantidad());
    productoService.actualizarStock(producto);
    
    return detalleGuardado;
}
```

### **Características del Sistema de Stock:**
- ✅ **Validación previa:** No permite ventas sin stock
- ✅ **Actualización atómica:** Stock se descuenta inmediatamente
- ✅ **Optimización:** Método directo `actualizarStock()` evita conversiones DTO
- ✅ **Flexibilidad de precio:** Permite precios especiales por detalle

---

## 🎭 **FacturaService: El Director de Orquesta**

### **Save() - La Sinfonía Completa:**

```java
public Factura save(FacturaDTO facturaDTO) {
    // 1. CREAR factura base (establece timestamp oficial)
    FacturaEntity factura = crearFactura(facturaDTO);
    
    // 2. PLACEHOLDER Mercado Pago (integración futura)
    if (esMercadoPago(facturaDTO.getFormaPagoId())) {
        // TODO: Magia de Mercado Pago aquí
        // procesarPagoMercadoPago(factura);
    }
    
    // 3. CONECTAR LAS TUBERÍAS 🔧
    asociarReparaciones(factura, facturaDTO.getReparacionIds());
    crearDetallesFactura(factura, facturaDTO.getProductos());
    
    // 4. DEVOLVER modelo completo con toda la información
    return devolverModelo(factura);
}
```

### **Análisis de Cada Componente:**

#### **1. crearFactura() - El Fundamento:**
```java
private FacturaEntity crearFactura(FacturaDTO facturaDTO) {
    // Resolver entidades de soporte
    UsuarioEntity usuario = resolverUsuario(facturaDTO.getUsuarioId());
    SedeEntity sede = resolverSede(facturaDTO.getSedeId());
    FormaPagoEntity formaPago = resolverFormaPago(facturaDTO.getFormaPagoId());
    
    // Crear factura con timestamp oficial
    FacturaEntity factura = new FacturaEntity();
    factura.setFecha(LocalDateTime.now());  // ← Momento EXACTO de facturación
    factura.setUsuario(usuario);
    factura.setSede(sede);
    factura.setFormaPago(formaPago);
    
    // Datos del cliente (todos opcionales)
    factura.setClienteNombre(facturaDTO.getClienteNombre());
    // ... resto de datos cliente
    
    return repository.save(factura);  // ← Genera ID para orquestación
}
```

#### **2. asociarReparaciones() - El Sellado:**
```java
private void asociarReparaciones(FacturaEntity factura, List<Long> reparacionIds) {
    if (reparacionIds == null || reparacionIds.isEmpty()) return;
    
    for (Long reparacionId : reparacionIds) {
        // Buscar reparación existente
        ReparacionEntity reparacion = reparacionService.findEntityById(reparacionId)
            .orElseThrow(() -> new EntryNotFoundException("Reparacion no encontrada"));
            
        // Validar que no esté ya facturada
        if (reparacion.getFactura() != null) {
            throw new ConflictiveStateException("Reparacion ya facturada");
        }
        
        // "SELLAR" la reparación con la factura
        reparacion.setFactura(factura);
        reparacion.setFechaEntrega(factura.getFecha());  // ← Lógica de negocio perfecta
        
        reparacionService.actualizarReparacion(reparacion);
    }
}
```

#### **3. crearDetallesFactura() - La Iteración Eficiente:**
```java
private void crearDetallesFactura(FacturaEntity factura, List<DetalleFacturaDTO> productos) {
    if (productos == null || productos.isEmpty()) return;
    
    // Iteración simple - DetalleFacturaService hace toda la magia
    productos.forEach(dto -> detalleFacturaService.save(dto, factura));
}
```

#### **4. devolverModelo() - El Agregador:**
```java
private Factura devolverModelo(FacturaEntity facturaEntity) {
    Factura modelo = new Factura();
    
    // Datos básicos + entidades de soporte
    modelo.setId(facturaEntity.getId());
    modelo.setFecha(facturaEntity.getFecha());
    modelo.setUsuario(modelMapper.map(facturaEntity.getUsuario(), Usuario.class));
    // ... resto de entidades de soporte
    
    // BUSCAR contenido relacionado activamente
    List<DetalleFactura> detalles = detalleFacturaService.findByFacturaId(facturaEntity.getId());
    List<Reparacion> reparaciones = reparacionService.findByFacturaId(facturaEntity.getId());
    
    modelo.setDetalles(detalles);
    modelo.setReparaciones(reparaciones);
    
    return modelo;
}
```

---

## 📊 **Testing Épico - El Momento de la Verdad**

### **Casos de Prueba Reales:**

#### **JSON de Input:**
```json
{
  "usuarioId": 1,
  "sedeId": 1,
  "formaPagoId": 1,
  "clienteNombre": "Juan Carlos",
  "clienteEmail": "juan@email.com",
  "reparacionIds": [1, 2],
  "productos": [
    {
      "productoId": 1,
      "cantidad": 2,
      "precio": 1400.0
    },
    {
      "productoId": 3,
      "cantidad": 1
    }
  ]
}
```

#### **Resultado BEFORE/AFTER:**

**BEFORE:**
```sql
-- Reparaciones sin facturar
SELECT id, factura_id, fecha_entrega FROM Reparaciones WHERE id IN (1,2);
-- 1 | null | 2025-11-10 17:00:00
-- 2 | null | 2025-06-10 16:00:00

-- Stock original
SELECT id, nombre, stock FROM Productos WHERE id IN (1,3);
-- 1 | Guitarra Les Paul | 10
-- 3 | Teclado PSR-E373  | 15
```

**AFTER:**
```sql
-- Reparaciones SELLADAS con misma fecha de factura
-- 1 | 1 | 2025-06-12T07:31:56.793376
-- 2 | 1 | 2025-06-12T07:31:56.793376

-- Stock ACTUALIZADO automáticamente  
-- 1 | Guitarra Les Paul | 8  (10-2)
-- 3 | Teclado PSR-E373  | 14 (15-1)
```

### **La Consulta Épica - Todo Junto:**
```sql
SELECT
    f.id as factura_id,
    f.fecha,
    f.cliente_nombre,
    'PRODUCTO' as tipo,
    p.nombre as item,
    df.cantidad,
    df.precio
FROM Facturas f
JOIN Detalle_Factura df ON f.id = df.factura_id
JOIN Productos p ON df.producto_id = p.id

UNION ALL

SELECT
    f.id as factura_id,
    f.fecha,
    f.cliente_nombre,
    'REPARACION' as tipo,
    r.detalles as item,
    1 as cantidad,
    r.precio
FROM Facturas f
JOIN Reparaciones r ON f.id = r.factura_id
ORDER BY tipo, item;
```

**Resultado:**
```
factura_id | cliente_nombre | tipo       | item                          | cantidad | precio
-----------|----------------|------------|-------------------------------|----------|--------
1          | Juan Carlos    | PRODUCTO   | Guitarra Les Paul            | 2        | 1400.0
1          | Juan Carlos    | PRODUCTO   | Teclado PSR-E373             | 1        | 800.0
1          | Juan Carlos    | REPARACION | Guitarra clásica - mantenim. | 1        | 120.0
1          | Juan Carlos    | REPARACION | Guitarra acústica - clavijas | 1        | 80.0
```

---

## 🏛️ **Specifications Avanzadas: Business Intelligence**

### **El Problema que Resolvimos:**

```java
// ❌ ENFOQUE TRADICIONAL (malo):
@GetMapping("/dashboard/ventas-por-mes")           // Endpoint específico
@GetMapping("/dashboard/ventas-por-vendedor")     // Otro endpoint específico  
@GetMapping("/dashboard/productos-vs-servicios") // Otro más...
// ... infinitas combinaciones

// ✅ NUESTRO ENFOQUE (genial):
@GetMapping("/facturas/page?fechaDesde=X&usuarioId=Y&tieneProductos=Z")  // ¡Flexibilidad infinita!
```

### **Las 9 Specifications Implementadas:**

#### **1. byMontoRango() - La Joya Compleja:**
```java
public Specification<FacturaEntity> byMontoRango(Double montoMin, Double montoMax) {
    return (root, query, builder) -> {
        // Subquery 1: SUM de productos (precio * cantidad)
        Subquery<Double> sumDetalles = query.subquery(Double.class);
        Root<DetalleFacturaEntity> detalleRoot = sumDetalles.from(DetalleFacturaEntity.class);
        sumDetalles.select(
            builder.coalesce(
                builder.sum(
                    builder.prod(detalleRoot.get("precio"), detalleRoot.get("cantidad"))
                ), 0.0
            )
        ).where(
            builder.equal(detalleRoot.get("factura").get("id"), root.get("id"))
        );

        // Subquery 2: SUM de servicios (precio directo)
        Subquery<Double> sumReparaciones = query.subquery(Double.class);
        Root<ReparacionEntity> reparacionRoot = sumReparaciones.from(ReparacionEntity.class);
        sumReparaciones.select(
            builder.coalesce(builder.sum(reparacionRoot.get("precio")), 0.0)
        ).where(
            builder.equal(reparacionRoot.get("factura").get("id"), root.get("id"))
        );

        // TOTAL = productos + servicios
        var montoTotal = builder.sum(sumDetalles, sumReparaciones);
        
        return builder.between(montoTotal, montoMin, montoMax);
    };
}
```

#### **2. byTieneReparaciones() - EXISTS Elegante:**
```java
public Specification<FacturaEntity> byTieneReparaciones(Boolean tieneReparaciones) {
    return (root, query, builder) -> {
        if (tieneReparaciones == null) return builder.conjunction();

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<ReparacionEntity> reparacionRoot = subquery.from(ReparacionEntity.class);
        subquery.select(builder.literal(1L))
                .where(builder.equal(reparacionRoot.get("factura").get("id"), root.get("id")));

        return tieneReparaciones ? builder.exists(subquery) : builder.not(builder.exists(subquery));
    };
}
```

#### **3. byCantidadItemsMin() - Lógica de Negocio Pura:**
```java
public Specification<FacturaEntity> byCantidadItemsMin(Integer cantidadItemsMin) {
    return (root, query, builder) -> {
        if (cantidadItemsMin == null) return builder.conjunction();

        // Solo cuenta PRODUCTOS (items físicos), no servicios
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<DetalleFacturaEntity> detalleRoot = subquery.from(DetalleFacturaEntity.class);
        subquery.select(
            builder.coalesce(
                builder.sum(detalleRoot.get("cantidad")), 0
            )
        ).where(
            builder.equal(detalleRoot.get("factura").get("id"), root.get("id"))
        );

        return builder.greaterThanOrEqualTo(subquery, cantidadItemsMin);
    };
}
```

### **Casos de Uso Dashboard ÉPICOS:**
```bash
# Dashboard: "Solo ventas de mostrador"
GET /facturas/page?tieneProductos=true&tieneReparaciones=false

# Dashboard: "Ventas grandes de Juan"  
GET /facturas/page?usuarioId=1&montoMin=1000

# Dashboard: "Compras mayoristas en efectivo"
GET /facturas/page?cantidadItemsMin=5&formaPagoId=1

# Dashboard: "Mix productos + servicios por período"
GET /facturas/page?fechaDesde=2025-06-01&tieneProductos=true&tieneReparaciones=true
```

---

## ⚠️ **Delete() - La Funcionalidad Pendiente**

### **Lógica de Cancelación Planificada:**

```java
// TODO: Implementar en próxima iteración
public void delete(Long facturaId) {
    // 1. Marcar factura como cancelada
    facturaEntity.setActivo(false);
    
    // 2. RESTAURAR stock de productos automáticamente
    for (DetalleFactura detalle : facturaEntity.getDetalles()) {
        ProductoEntity producto = detalle.getProducto();
        producto.setStock(producto.getStock() + detalle.getCantidad());
        productoService.actualizarStock(producto);
    }
    
    // 3. "DESSELLAR" reparaciones (vuelven a ser editables)
    for (Reparacion reparacion : facturaEntity.getReparaciones()) {
        reparacionEntity.setFactura(null);
        reparacionEntity.setFechaEntrega(null);
        reparacionService.actualizarReparacion(reparacionEntity);
    }
}
```

### **Validaciones Necesarias:**
- ✅ Solo facturas activas pueden cancelarse
- ✅ Timeouts de cancelación (ej: no cancelar después de 24hs)
- ✅ Logs de auditoría para cancelaciones
- ✅ Notificaciones al cliente (futuro)

---

## 🎯 **Decisiones Arquitectónicas Clave**

### **1. ¿Por qué FacturaEntity no "contiene" los datos?**

**Factura como HUB de referencia:**
```java
// FacturaEntity - El HUB central
private Long id;                    // ← CLAVE que conecta todo
private LocalDateTime fecha;        // ← Timestamp oficial
private UsuarioEntity usuario;      // ← Quien vendió  
private FormaPagoEntity formaPago;  // ← Como se pagó

// Los datos relacionados viven en sus propias tablas:
// - DetalleFactura → referencia factura.id
// - Reparacion → referencia factura.id
```

**Beneficios:**
- ✅ **Normalización correcta:** Sin duplicación de datos
- ✅ **Queries eficientes:** JOIN solo cuando necesario
- ✅ **Escalabilidad:** Fácil agregar nuevos tipos de "contenido"
- ✅ **Inmutabilidad:** Factura simple, contenido en tablas especializadas

### **2. ¿Por qué sobrecarga en lugar de parámetros opcionales?**

```java
// ✅ ELEGANTE: Métodos especializados
Reparacion save(ReparacionDTO dto);                    // Independiente
Reparacion save(ReparacionDTO dto, FacturaEntity f);   // Orquestado

// ❌ CONFUSO: Parámetros opcionales  
Reparacion save(ReparacionDTO dto, FacturaEntity factura = null);
```

**Beneficios:**
- ✅ **Intención clara:** El método dice exactamente qué hace
- ✅ **Type safety:** Compilador valida parámetros correctos
- ✅ **Backward compatibility:** Código existente sigue funcionando
- ✅ **Documentación implícita:** El nombre del método es autodocumentado

### **3. ¿Por qué devolverModelo() en lugar de JPA lazy loading?**

```java
// ✅ NUESTRO ENFOQUE: Control explícito
return devolverModelo(factura);  // Trae EXACTAMENTE lo que necesitamos

// ❌ ALTERNATIVA: JPA automático
return factura;  // ¿Qué trae? ¿Cuántas queries? ¿N+1 problem?
```

**Beneficios:**
- ✅ **Performance predecible:** Sabemos exactamente qué queries se ejecutan
- ✅ **Control total:** Decidimos qué incluir en cada caso
- ✅ **Sin N+1:** Una query por tipo de contenido relacionado
- ✅ **Debugging fácil:** Logs claros de qué se consulta

---

## 🏆 **Resultados Arquitectónicos**

### **Lo que Logramos:**
- ✅ **Orquestación perfecta** de 4+ services sin dependencias circulares
- ✅ **Gestión automática** de stock, fechas y estado de reparaciones  
- ✅ **Inmutabilidad inteligente** preservando flexibilidad donde es necesaria
- ✅ **Business Intelligence** con un solo endpoint súper flexible
- ✅ **Lógica de negocio sólida** validada con datos reales
- ✅ **Extensibilidad máxima** para futuras integraciones (Mercado Pago, etc.)

### **Métricas de Éxito:**
- **Testing exhaustivo:** ✅ 3 tipos de facturas funcionando perfectamente
- **Performance:** ✅ Queries optimizadas con specifications inteligentes  
- **Mantenibilidad:** ✅ Código autodocumentado con patrones consistentes
- **Escalabilidad:** ✅ Fácil agregar nuevos tipos de contenido facturado

---

## 🚀 **El Futuro de Facturación**

### **Integraciones Planificadas:**
1. **Delete() con restauración automática** - Próxima implementación
2. **Mercado Pago** - API externa en placeholder existente
3. **Validation annotations** - Extraer validaciones de Services
4. **Auditoría avanzada** - Logs de todas las operaciones críticas
5. **Reportes complejos** - Aprovechar specifications para dashboards

### **Posibles Extensiones:**
- **Facturación recurrente** - Para suscripciones o servicios mensuales
- **Descuentos y promociones** - Sistema de cupones
- **Facturación internacional** - Multi-moneda y tasas de cambio
- **Integración contable** - Export a sistemas de contabilidad

---

## 🎉 **Conclusión**

**FacturaService** no es solo el final del desarrollo backend - es la **culminación de una arquitectura** que demuestra cómo patrones simples y bien aplicados pueden manejar lógica de negocio compleja sin sacrificar claridad, performance o mantenibilidad.

Cada línea de código en esta implementación construye sobre las decisiones arquitectónicas de las 3 partes anteriores, demostrando que una base sólida permite complejidad elegante en los niveles superiores.

**"El corazón ya late. Ahora toca la carrocería."**

---

*Esta documentación marca el final del desarrollo del motor y el inicio de la fase de integración y pulimiento. El futuro es brillante.* ✨