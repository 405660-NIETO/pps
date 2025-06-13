package tup.pps.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        // Públicos (sin login)
                        .requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Solo ADMINISTRADOR
                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")

                        // Todo lo demás necesita estar logueado
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.permitAll())  // Habilita login automático
                .csrf(csrf -> csrf.disable())        // Para testing (después habilitamos)
                .headers(headers -> headers.frameOptions().disable()) // Para H2 console
                .build();
    }
}