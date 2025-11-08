package com.chifawufu.sistema_ventas.config;

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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

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

    // --- Configuración Global de CORS (Maneja el CrossOrigin por nosotros) ---
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/**") // Aplica a toda tu API
                    .allowedOrigins("http://localhost:5173") // Tu frontend
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true); // ¡La parte más importante!
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. APLICAMOS LA CONFIGURACIÓN GLOBAL DE CORS
            .cors(Customizer.withDefaults())
            
            // 2. DESACTIVAMOS CSRF (común para APIs)
            .csrf(csrf -> csrf.disable()) 
            
            // 3. ¡AQUÍ ESTÁ LA CORRECCIÓN!
            .authorizeHttpRequests(auth -> auth
                // Solo permitimos que la PÁGINA DE LOGIN sea pública
                .requestMatchers("/api/usuarios/login").permitAll()
                
                // Todo lo demás (incluyendo /api/usuarios) requiere autenticación
                .anyRequest().authenticated()
            )
            
            // 4. MANEJADOR DE ERRORES
            // En lugar del pop-up, solo devuelve un error 401
            .exceptionHandling(e -> 
                e.authenticationEntryPoint((request, response, authException) -> 
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado")
                )
            );
        
        // 5. ¡Ya no usamos httpBasic()!

        return http.build();
    }
}