package com.chifawufu.sistema_ventas.config;

// --- 1. AÑADE ESTAS IMPORTACIONES ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List; // Asegúrate de importar java.util.List
import org.springframework.http.HttpMethod; // Para las peticiones OPTIONS
// ---------------------------------

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer; 
// --- QUITA ESTAS IMPORTACIONES (YA NO SE USAN) ---
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// import org.springframework.lang.NonNull;
// ---------------------------------------------
import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- 2. HEMOS QUITADO EL BEAN 'WebMvcConfigurer corsConfigurer()' ---
    // ... ya no es necesario ...

    // --- 3. AÑADIMOS ESTE NUEVO BEAN PARA CONFIGURAR CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Define de dónde permites solicitudes (tu frontend de React)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        
        // Define los métodos HTTP que permites
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permite todas las cabeceras (como "Content-Type", "Authorization", etc.)
        configuration.setAllowedHeaders(List.of("*"));
        
        // ¡LA PARTE MÁS IMPORTANTE! Permite que el navegador envíe cookies de sesión
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a toda tu API (cualquier ruta bajo /api/)
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
    // --------------------------------------------------------

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 4. APLICA LA CONFIGURACIÓN CORS DEL BEAN DE ARRIBA
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 5. DESACTIVAMOS CSRF (común para APIs)
            .csrf(csrf -> csrf.disable()) 
            
            // 6. ¡REGLAS DE AUTORIZACIÓN ACTUALIZADAS!
            .authorizeHttpRequests(auth -> auth
                // Permite todas las peticiones "pre-vuelo" OPTIONS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Permite la URL de login
                .requestMatchers("/api/usuarios/login").permitAll()
                
                // Exige autenticación para CUALQUIER OTRA petición
                .anyRequest().authenticated()
            )
            
            // 7. MANEJADOR DE ERRORES (devuelve 401 en lugar de un pop-up)
            .exceptionHandling(e -> 
                e.authenticationEntryPoint((request, response, authException) -> 
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado")
                )
            );
        
        return http.build();
    }
}
