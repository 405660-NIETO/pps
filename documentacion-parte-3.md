# Arquitectura Avanzada: Productos, Reparaciones y Facturación

## Resumen Ejecutivo

Después de consolidar las **Tablas Soporte** (Patrón A y B), desarrollamos la arquitectura avanzada para **Productos**, **Reparaciones** y **DetalleFactura** - las entidades más complejas del sistema que manejan relaciones muchos-a-muchos, orquestación de servicios y gestión de stock automática.

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

## 📅 **Gestión Global de Fechas (ISO Format)**

### **Configuración aplicada:**

```properties
# application.properties
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.date-format=yyyy-MM-dd'T'HH:mm:ss
```

```java
// MappersConfig.java
@Bean
public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
}
```

### **Resultado:**
```json
// ❌ ANTES:
"fechaCreacion": [2025,6,12,0,59,10,175250000]

// ✅ DESPUÉS:
"fechaCreacion": "2025-06-12T01:11:02.238247"
```

---

## 🎭 **Patrón de Orquestación: FacturaService como Director**

### **Arquitectura de Coordinación:**

```java
public Factura save(FacturaDTO facturaDTO, List<Long> reparacionIds, List<DetalleFacturaDTO> productos) {
    
    // 1. CREAR factura base
    FacturaEntity factura = crearFactura(facturaDTO);
    
    // 2. VALIDAR forma de pago (placeholder Mercado Pago)
    if (esMercadoPago(facturaDTO.getFormaPagoId())) {
        // TODO: Integración Mercado Pago
        // procesarPagoMercadoPago(factura);
    }
    
    // 3. ORQUESTAR sub-entidades
    asociarReparaciones(factura, reparacionIds);       // Usar sobrecarga
    crearDetallesFactura(factura, productos);          // Con stock automático
    
    return devolverModelo(factura);
}
```

### **Beneficios de la Orquestación:**
- ✅ **Separación clara:** Cada service maneja su dominio
- ✅ **Reutilización:** Aprovecha métodos existentes
- ✅ **Extensibilidad:** Fácil agregar validaciones/integraciones
- ✅ **Atomicidad:** Todo o nada en una transacción

---

## 🏛️ **Specifications: Filtros Avanzados sin Endpoints Redundantes**

### **Problema Resuelto:**

```java
// ❌ ENFOQUE TRADICIONAL (malo):
@GetMapping("/by-fecha")           // Endpoint específico
@GetMapping("/by-nombre")          // Otro endpoint específico  
@GetMapping("/by-precio")          // Otro más...
// ... infinitas combinaciones

// ✅ NUESTRO ENFOQUE (bueno):
@GetMapping("/page?nombre=X&fechaInicio=Y&precio=Z&activo=true")  // Un endpoint flexible
```

### **Ejemplo de Specification Compleja (Reparaciones):**

```java
public Specification<ReparacionEntity> byTrabajos(List<String> nombresTrabajos) {
    return (root, query, builder) -> {
        // Subquery que cuenta cuántos trabajos coinciden
        Subquery<Long> subquery = query.subquery(Long.class);
        // ... lógica compleja con COUNT()
        
        // La reparación debe tener TODOS los trabajos solicitados (AND lógico)
        return builder.equal(subquery, (long) nombresTrabajos.size());
    };
}
```

### **Beneficios:**
- ✅ **Reutilizable:** Mismo patrón en todas las entidades
- ✅ **Extensible:** Agregar filtros no requiere nuevos endpoints
- ✅ **Performance:** Queries optimizadas con JPA Criteria
- ✅ **Frontend-friendly:** Máxima flexibilidad de combinaciones

---

## 🚀 **Decisiones Arquitectónicas Clave**

### **1. Auto-creación Inteligente:**
- ✅ **SÍ auto-crear:** Categorías, Marcas, Trabajos (datos catalogables)
- ❌ **NO auto-crear:** Usuarios, Sedes, FormaPago (entidades de negocio)

### **2. Inmutabilidad Selectiva:**
- ✅ **Inmutable:** Facturas, DetalleFactura (documentos legales)
- ✅ **Mutable:** Productos, Reparaciones (hasta cierto punto)

### **3. Borrado Lógico Universal:**
- ✅ **Historial completo:** Nunca se pierde información
- ✅ **Reactivación:** Entidades pueden volver a activarse
- ✅ **Integridad:** Referencias históricas mantenidas

### **4. Separación de Responsabilidades:**
- **Tablas Soporte:** Proveen datos únicos y reutilizables
- **Productos/Reparaciones:** Manejan lógica de negocio compleja
- **Facturación:** Orquesta y convierte en documentos inmutables

---

## 🎯 **Estado Actual del Proyecto**

### **✅ Completado al 100%:**
- **Tablas Soporte** (Patrón A y B)
- **Productos** (CRUD completo + relaciones complejas)
- **Reparaciones** (CRUD completo + sobrecarga + validaciones)
- **DetalleFactura** (save + findById + gestión de stock)

### **🚧 En Desarrollo:**
- **FacturaService** (orquestación + placeholder Mercado Pago)

### **📋 Próximos Pasos:**
1. **Completar FacturaService** (save, findAll con specs, delete)
2. **UsuarioService CRUD completo**
3. **Spring Security** (autenticación por roles)
4. **Validation annotations** (extraer validaciones de Services)
5. **Integración Mercado Pago**

---

## 🏆 **Resultados Arquitectónicos**

- ✅ **Patrón escalable** probado en múltiples entidades
- ✅ **Performance optimizada** con Specifications avanzadas
- ✅ **Integridad de datos** garantizada con validaciones de negocio
- ✅ **Código limpio** con separación clara de responsabilidades
- ✅ **Flexibilidad máxima** para frontend con filtros combinables
- ✅ **Base sólida** para completar el 25% restante del proyecto

**El camino está pavimentado para una integración exitosa con Angular 18 y el resto de funcionalidades del e-commerce.**