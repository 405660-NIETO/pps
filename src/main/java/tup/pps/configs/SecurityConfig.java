package tup.pps.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.and())
                .authorizeHttpRequests(auth -> auth
                        // PÚBLICOS (sin login)
                        .requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/ping").permitAll()  // Easter egg

                        // USUARIOS - Control estricto
                        .requestMatchers(HttpMethod.GET, "/usuarios/*", "/usuarios/page").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/*").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/usuarios").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/*").hasRole("ADMINISTRADOR")

                        // ROLES - Consultas empleados, gestión admin
                        .requestMatchers(HttpMethod.GET, "/usuarios/roles", "/usuarios/roles/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.POST, "/usuarios/roles").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/roles").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/roles/*").hasRole("ADMINISTRADOR")

                        // SEDES - Consultas públicas, gestión admin
                        .requestMatchers(HttpMethod.GET, "/sedes", "/sedes/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sedes").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/sedes").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/sedes/*").hasRole("ADMINISTRADOR")

                        // REPARACIONES - POST público, gestión empleados+luthiers
                        .requestMatchers(HttpMethod.POST, "/reparaciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reparaciones/*", "/reparaciones/page").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.PUT, "/reparaciones/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.DELETE, "/reparaciones/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")

                        // PRODUCTOS - Consultas públicas, gestión empleados+luthiers
                        .requestMatchers(HttpMethod.GET, "/productos/*", "/productos/page").permitAll()
                        .requestMatchers(HttpMethod.POST, "/productos").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.PUT, "/productos/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.DELETE, "/productos/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")

                        // FORMAS DE PAGO - Consultas públicas, gestión admin
                        .requestMatchers(HttpMethod.GET, "/facturas/formapago", "/facturas/formapago/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/facturas/formapago").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/facturas/formapago").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/facturas/formapago/*").hasRole("ADMINISTRADOR")

                        // TRABAJOS - Gestión empleados+luthiers
                        .requestMatchers(HttpMethod.GET, "/reparaciones/trabajos/*", "/reparaciones/trabajos/page").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.POST, "/reparaciones/trabajos").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.DELETE, "/reparaciones/trabajos/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")

                        // MARCAS - Consultas públicas, gestión empleados+luthiers
                        .requestMatchers(HttpMethod.GET, "/productos/marcas/*", "/productos/marcas/page").permitAll()
                        .requestMatchers(HttpMethod.POST, "/productos/marcas").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.DELETE, "/productos/marcas/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")

                        // CATEGORÍAS - Consultas públicas, gestión empleados+luthiers
                        .requestMatchers(HttpMethod.GET, "/productos/categorias/*", "/productos/categorias/page").permitAll()
                        .requestMatchers(HttpMethod.POST, "/productos/categorias").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.DELETE, "/productos/categorias/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")

                        // FACTURAS - POST público, gestión empleados+luthiers
                        .requestMatchers(HttpMethod.POST, "/facturas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/facturas/page").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")
                        .requestMatchers(HttpMethod.DELETE, "/facturas/*").hasAnyRole("ADMINISTRADOR", "EMPLEADO", "LUTHIER")

                        // Todo lo demás requiere auth
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            // SIEMPRE devolver JSON para Angular, nunca redirect
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\":true,\"message\":\"Login successful\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            // Error handler JSON para Angular
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Invalid credentials\"}");
                        })
                )

                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Logout handler JSON
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\":true,\"message\":\"Logout successful\"}");
                        })
                )
                .headers(headers -> headers.frameOptions().disable())
                .build();
    }
}