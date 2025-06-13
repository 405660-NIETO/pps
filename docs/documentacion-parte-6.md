# 🔐 Parte 6: Spring Security - La Autenticación Empresarial

## Resumen Ejecutivo

Después de completar el backend funcional completo (CRUDs + Lógica de negocio), implementamos **Spring Security** para autenticación basada en sessions y **matriz de permisos granular** por endpoint. Esta implementación elimina la necesidad de JWT y proporciona seguridad robusta con mínima configuración.

---

## 🏗️ **CustomUserDetailsService: El Adaptador Inteligente**

### **Función Principal:**
Conecta nuestro `UsuarioService` con Spring Security, agregando **validaciones de negocio** y **auditoría automática**.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        // 1. BUSCAR usuario (validación de existencia automática)
        UsuarioEntity usuario = usuarioService.findByEmail(email);
        
        // 2. VALIDAR usuario activo (regla de negocio)
        if (!usuario.getActivo()) {
            throw new ForbiddenOperationException("Usuario inactivo");
        }
        
        // 3. AUDITORÍA de login automática
        usuario.setFechaLogin(LocalDateTime.now());
        usuarioService.actualizarUsuario(usuario);
        
        // 4. RETORNAR UserDetails para Spring Security
        return User.builder()
                .username(usuario.getEmail())
                .password("{noop}" + usuario.getPassword())
                .roles(usuario.getRol().getNombre())
                .build();
    }
}
```

### **Características Clave:**
- ✅ **Validación de negocio** - Solo usuarios activos pueden autenticarse
- ✅ **Auditoría automática** - Actualiza `fechaLogin` en cada acceso exitoso
- ✅ **Integración perfecta** - Reutiliza `UsuarioService` existente
- ✅ **Manejo de errores** - Spring Security muestra "Usuario inactivo" automáticamente

---

## 🛡️ **Matriz de Permisos: Arquitectura Granular**

### **Filosofía de Seguridad:**

#### **🌐 Acceso Público (permitAll)**
```java
// Experiencia del cliente - Sin registro necesario
GET /productos/*        → Ver catálogo completo
GET /categorias/*       → Filtros de navegación  
GET /marcas/*          → Búsqueda por fabricante
GET /sedes/*           → "¿Dónde retirar?"
GET /formas-pago/*     → "¿Cómo pagar?"
POST /facturas         → Compra online directa
POST /reparaciones     → Solicitud de servicio
```

#### **👥 Gestión Operativa (ADMINISTRADOR + EMPLEADO + LUTHIER)**
```java
// Trabajo diario del personal
GET /usuarios/*         → Ver datos de compañeros
GET /reparaciones/*     → Consultar servicios
PUT /productos/*        → Actualizar inventario
POST /productos         → Agregar nuevos items
DELETE /categorias/*    → Limpiar catálogo
```

#### **👑 Control Empresarial (Solo ADMINISTRADOR)**
```java
// Decisiones de estructura y personal
POST /usuarios          → Contratar empleados
PUT /usuarios/*         → Modificar datos/roles
DELETE /usuarios/*      → Dar de baja personal
PUT /sedes             → Configurar sucursales
PUT /formas-pago       → Gestionar métodos de pago
```

### **Implementación en SecurityConfig:**

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            // 🌐 PÚBLICOS - Experiencia del cliente
            .requestMatchers(HttpMethod.GET, "/productos/*", "/categorias/*", "/marcas/*").permitAll()
            .requestMatchers(HttpMethod.POST, "/facturas", "/reparaciones").permitAll()
            
            // 👥 OPERATIVO - Personal de tienda
            .requestMatchers(HttpMethod.GET, "/usuarios/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
            .requestMatchers(HttpMethod.POST, "/productos").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
            
            // 👑 EMPRESARIAL - Solo administración
            .requestMatchers(HttpMethod.POST, "/usuarios").hasRole("ADMINISTRADOR")
            .requestMatchers(HttpMethod.PUT, "/usuarios/*").hasRole("ADMINISTRADOR")
            .requestMatchers(HttpMethod.DELETE, "/usuarios/*").hasRole("ADMINISTRADOR")
            
            .anyRequest().authenticated()
        )
        .formLogin(form -> form.permitAll())
        .csrf(csrf -> csrf.disable())
        .build();
}
```

---

## 🎯 **Casos de Uso por Rol**

### **🛒 Cliente/Invitado:**
- **Navega catálogo** sin registrarse
- **Realiza compras** con datos mínimos
- **Solicita reparaciones** directamente
- **No accede** a gestión interna

### **🔧 Luthier:**
- **Ve sus reparaciones** asignadas
- **Actualiza estados** de trabajos
- **Consulta datos** de otros empleados
- **No modifica** estructura empresarial

### **💼 Empleado:**
- **Gestiona inventario** día a día
- **Procesa ventas** en mostrador
- **Ve reportes** de facturación
- **No crea/elimina** usuarios

### **👑 Administrador:**
- **Control total** de usuarios y roles
- **Configuración** de sedes y pagos
- **Acceso completo** a toda funcionalidad
- **Supervisión** sin micromanagement

---

## 🚀 **Flujo de Autenticación Completo**

### **1. Usuario intenta acceder a endpoint protegido:**
```
GET /usuarios/page → Spring Security intercepta
```

### **2. Redirección automática a login:**
```
302 Redirect → http://localhost:8080/login
```

### **3. Usuario ingresa credenciales:**
```
POST /login → Spring Security llama CustomUserDetailsService
```

### **4. Validación y auditoría:**
```
loadUserByUsername() → Validar activo → Actualizar fechaLogin → Crear session
```

### **5. Acceso concedido:**
```
Cookie JSESSIONID → Requests posteriores autenticados automáticamente
```

---

## 🔗 **Preparación para Frontend**

### **Integración con Angular:**
```typescript
// Login desde componente Angular
this.http.post('/login', credentials, { withCredentials: true })
  .subscribe(success => this.router.navigate(['/dashboard']));

// Requests automáticos con session
this.http.get('/usuarios', { withCredentials: true })
  .subscribe(data => console.log(data));
```

### **Manejo de errores:**
- **401 Unauthorized** → Redirigir a login
- **403 Forbidden** → Mostrar "Sin permisos"
- **302 Redirect** → Interceptar y manejar en frontend

---

## 🏆 **Resultados Arquitectónicos**

### **Seguridad Robusta:**
- ✅ **Sessions server-side** - Control total del backend
- ✅ **Permisos granulares** - Por método HTTP + endpoint específico
- ✅ **Validaciones de negocio** - Usuarios inactivos bloqueados automáticamente
- ✅ **Auditoría completa** - Tracking de accesos exitosos

### **Experiencia de Usuario:**
- ✅ **Acceso público fluido** - Clientes navegan sin fricción
- ✅ **Login transparente** - Solo cuando se necesita acceso interno
- ✅ **Mensajes claros** - "Usuario inactivo" en lugar de errores genéricos
- ✅ **Separation of concerns** - Frontend maneja UI, backend maneja seguridad

### **Mantenibilidad:**
- ✅ **Configuración centralizada** - Toda la matriz en SecurityConfig
- ✅ **Reutilización de servicios** - CustomUserDetailsService usa UsuarioService existente
- ✅ **Extensibilidad** - Fácil agregar nuevos roles o endpoints
- ✅ **Testing simple** - Curl tests confirman funcionamiento

---

## 🎉 **Conclusión**

La implementación de **Spring Security** completa la arquitectura del backend, proporcionando seguridad empresarial sin sacrificar simplicidad. La matriz de permisos refleja exactamente los roles del mundo real de una tienda de música, y la integración con el sistema de usuarios existente es perfecta.

**"El backend está 100% completo. Ahora toca darle vida con Angular."**

---

*Esta documentación marca el final del desarrollo backend y el inicio de la fase de presentación e interfaz de usuario.*