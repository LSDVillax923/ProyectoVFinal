package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.demo.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

     public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ── Públicos ─────────────────────────────────────────────────
                .requestMatchers("/h2/**", "/api/auth/**").permitAll()
                .requestMatchers("/api/clientes/login", "/api/veterinarios/login", "/api/admins/login").permitAll()
                // Registro de cliente (signup) sin token
                .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()

                // ── Admins: solo ADMIN ───────────────────────────────────────
                .requestMatchers("/api/admins/**").hasRole("ADMIN")

                // ── Veterinarios ─────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/veterinarios/**").authenticated()
                .requestMatchers("/api/veterinarios/**").hasRole("ADMIN")

                // ── Clientes (lectura/edición admin+vet, escrituras restantes admin+vet) ──
                .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "VETERINARIO")

                // ── Mascotas: lectura cualquier autenticado; escritura ADMIN/VET; CLIENTE solo en sus propias mascotas (validar en service) ──
                .requestMatchers(HttpMethod.GET, "/api/mascotas/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/mascotas/**").hasAnyRole("ADMIN", "VETERINARIO", "CLIENTE")
                .requestMatchers(HttpMethod.PUT, "/api/mascotas/**").hasAnyRole("ADMIN", "VETERINARIO", "CLIENTE")
                .requestMatchers(HttpMethod.PATCH, "/api/mascotas/**").hasAnyRole("ADMIN", "VETERINARIO")
                .requestMatchers(HttpMethod.DELETE, "/api/mascotas/**").hasAnyRole("ADMIN", "VETERINARIO")

                // ── Citas: cualquier autenticado puede consultarlas/agendarlas ─
                .requestMatchers("/api/citas/**").authenticated()

                // ── Tratamientos: lectura cualquier autenticado; escritura ADMIN/VET ──
                .requestMatchers(HttpMethod.GET, "/api/tratamientos/**").authenticated()
                .requestMatchers("/api/tratamientos/**").hasAnyRole("ADMIN", "VETERINARIO")
                .requestMatchers(HttpMethod.GET, "/api/tratamiento-drogas/**").authenticated()
                .requestMatchers("/api/tratamiento-drogas/**").hasAnyRole("ADMIN", "VETERINARIO")

                // ── Drogas: lectura cualquier autenticado; crear solo ADMIN; editar ADMIN+VET ──
                .requestMatchers(HttpMethod.GET, "/api/drogas/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/drogas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/drogas/**").hasRole("ADMIN")
                .requestMatchers("/api/drogas/**").hasAnyRole("ADMIN", "VETERINARIO")

                // ── Resto ────────────────────────────────────────────────────
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(h -> h.frameOptions(f -> f.disable()));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}