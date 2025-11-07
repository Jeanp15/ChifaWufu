package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.model.Usuario;
import com.chifawufu.sistema_ventas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Imports para el NUEVO login
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000") // Para conectar con el frontend
public class UsuarioController {
    
    // 1. FALTABA EL @AUTOWIRED AQUÍ
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @GetMapping
    @PreAuthorize("hasRole('Administrador')")
    public List<Usuario> getAllUsuarios() {
        return usuarioService.findAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('Administrador')")
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            Usuario usuarioExistente = usuario.get();
            usuarioExistente.setNombreUsuario(usuarioDetails.getNombreUsuario());
            usuarioExistente.setRol(usuarioDetails.getRol());
            usuarioExistente.setActivo(usuarioDetails.getActivo()); 
            
            // (Manejo de actualización de contraseña iría aquí)
            
            return ResponseEntity.ok(usuarioService.save(usuarioExistente));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // 2. MÉTODO LOGIN COMPLETAMENTE REEMPLAZADO
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest loginRequest) {
        
        // Esta línea crea la sesión de seguridad
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getNombreUsuario(), 
                loginRequest.getContraseña()
            )
        );
        
        // Le decimos a Spring que este usuario está ahora autenticado
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Buscamos al usuario (sin la contraseña) para devolverlo al frontend
        Optional<Usuario> usuario = usuarioService.findByUsername(authentication.getName());
        
        return usuario.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.status(401).build());
    }
    
    // Clase interna para el login
    public static class LoginRequest {
        private String nombreUsuario;
        private String contraseña;
        
        // Getters y Setters
        public String getNombreUsuario() { return nombreUsuario; }
        public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
        
        public String getContraseña() { return contraseña; }
        public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    }
}