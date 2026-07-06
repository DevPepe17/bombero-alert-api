package com.bomberos.emergencias.config;

import com.bomberos.emergencias.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración central de Spring Security con JWT y sesiones Stateless.
 *
 * Rutas públicas (sin token):
 *  - POST /api/auth/**         → Login y Registro
 *  - GET  /api/reportes/mapa  → Mapa público de incidentes (lectura)
 *
 * Rutas protegidas por rol:
 *  - /api/admin/**             → Solo ADMINISTRADOR
 *  - /api/operador/**          → OPERADOR y ADMINISTRADOR
 *  - /api/**                   → Cualquier usuario autenticado
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // Habilita @PreAuthorize en los controllers/services
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    // ─── Cadena de filtros principal ───────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (API REST stateless no necesita protección CSRF)
            .csrf(AbstractHttpConfigurer::disable)

            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Reglas de autorización por URL
            .authorizeHttpRequests(auth -> auth
                // ── Rutas públicas ──────────────────────────────────────────
                .requestMatchers(
                    "/api/auth/**",          // CU01 Registro + CU02 Login
                    "/api/reportes/mapa",    // Mapa público de incidentes
                    "/actuator/health",      // Health check (si se agrega Actuator)
                    "/v3/api-docs/**",       // Swagger (fase posterior)
                    "/swagger-ui/**"         // Swagger UI (fase posterior)
                ).permitAll()

                // ── Solo Administrador ──────────────────────────────────────
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")

                // ── Operador y Administrador ────────────────────────────────
                .requestMatchers("/api/operador/**")
                    .hasAnyRole("OPERADOR", "ADMINISTRADOR")

                // ── Cualquier usuario autenticado ───────────────────────────
                .anyRequest().authenticated()
            )

            // Sesiones STATELESS → no se crean sessions HTTP
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Proveedor de autenticación con BCrypt
            .authenticationProvider(authenticationProvider())

            // Agregar filtro JWT ANTES del filtro de username/password estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ─── Beans de autenticación ────────────────────────────────────────────────

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt con strength 12 (balance seguridad/rendimiento)
        return new BCryptPasswordEncoder(12);
    }

    // ─── Configuración CORS ────────────────────────────────────────────────────

    /**
     * Configuración CORS que permite el frontend (React/Vue/Leaflet).
     * En producción, reemplazar los orígenes permitidos con el dominio real.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (permite cualquier origen para evitar problemas con Vercel)
        config.setAllowedOriginPatterns(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
