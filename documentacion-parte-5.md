# 🔐 Usuarios & Spring Security

## 💡  Ideas a desarrollar

>**Spring Security** Puro para manejo de sessions en backend sin *JWT*

🍀 Pros:
1. ✅ Más simple - Sin tokens que manejar
2. ✅ Más seguro - Cookies HTTP-only
3. ✅ Spring maneja todo - Session management automático

🔎 ¿Por qué?
* Prototipo rápido - Menos código que escribir
* Un solo dominio - Frontend + Backend juntos
* Spring lo maneja - Sessions automáticas
* Menos bugs - No manejas tokens manualmente

### ⚙️ Operaciones de Usuarios:
* Login ✅ - Actualizar fechaLogin automáticamente
* Cambio password ✅ - Seguridad básica
* Update perfil ✅ - Nombre/apellido (email inmutable como PK)
* Rol management ✅ - Solo admin asigna roles

#### 🧩 Posible Logica:
1. 🔁 *Reactivacion:*
```java
// Flujo inteligente en save()
if (emailYaExiste && !activo) {
    // Reactivar + actualizar datos
    usuario.setActivo(true);
    usuario.setNombre(nuevoNombre);
    // ...
}
```

2. 📃 *findAll() - Admin necesita ver/gestionar usuarios, asignar roles, reactivar cuentas.* 
```java
// Para admin: gestión de usuarios
List<Usuario> findAll(String nombre, Boolean activo);  // Sin paginado
```

3. 🛡️ *Cosas que necesitaria Spring Security*
```java
// Método especial para autenticación
Optional<Usuario> findByEmailAndActivo(String email, boolean activo);

// Para Spring Security UserDetailsService
Usuario loadUserByUsername(String email);
```

4. 🌐 *Endpoints posibles*
```java
POST   /usuarios/register        // Registro + reactivación automática
POST   /usuarios/login          // Spring Security maneja esto
GET    /usuarios/me             // Perfil del usuario logueado  
PUT    /usuarios/me             // Actualizar perfil propio
PUT    /usuarios/{id}/rol       // Admin: cambiar rol de otro usuario
GET    /usuarios               // Admin: listar todos los usuarios
DELETE /usuarios/{id}          // Admin: dar de baja usuario
```
5. 🌊 *Idea para flujo en Spring Security*
   1. `Usuario` hace `login` → Spring valida credenciales
   2. Spring crea sesión automáticamente  
   3. Frontend accede a endpoints protegidos transparentemente
   4. `@PreAuthorize("hasRole('ADMIN')")` en métodos sensibles


6. 📦 *Posibles DTOs:*
```java
// UsuarioRegistroDTO
String email, password, nombre, apellido;

// UsuarioUpdateDTO  
String nombre, apellido, passwordActual, passwordNueva;

// UsuarioLoginDTO (si no usamos Spring forms)
String email, password;
```

7. 🔑 *Manejo de passwords para el prototipo*
```java
//Plain text para prototipo (inseguro pero rápido)
usuario.setPassword(plainPassword);
```

8. 🔀 *Posible flujo del servicio a futuro*
```java
// UsuarioService interface
Usuario save(UsuarioRegistroDTO dto);           // Registro + reactivación
Usuario update(Long id, UsuarioUpdateDTO dto);  // Perfil + password
Usuario findById(Long id);                      // Para Spring Security
Usuario findByEmail(String email);             // Login validation
List<Usuario> findAll(String nombre, Boolean activo);  // Admin management
void delete(Long id);                          // Dar de baja
```