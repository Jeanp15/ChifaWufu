package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.model.Usuario;
import com.chifawufu.sistema_ventas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.lang.NonNull; // 1. AÑADE ESTE IMPORT
import java.util.Objects;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    // ... (getAllUsuarios no cambia) ...
    public List<Usuario> getAllUsuarios() {
        return usuarioService.findAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    // 2. AÑADE @NonNull
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable @NonNull Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('Administrador')")
    // 3. AÑADE @NonNull
    public Usuario createUsuario(@RequestBody @NonNull Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    // 4. AÑADE @NonNull A AMBOS
    public ResponseEntity<Usuario> updateUsuario(@PathVariable @NonNull Long id, @RequestBody @NonNull Usuario usuarioDetails) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            Usuario usuarioExistente = usuario.get();
            usuarioExistente.setNombreUsuario(usuarioDetails.getNombreUsuario());
            usuarioExistente.setRol(usuarioDetails.getRol());
            usuarioExistente.setActivo(usuarioDetails.getActivo()); 
            
            return ResponseEntity.ok(usuarioService.actualizarUsuario(usuarioExistente));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    // 5. AÑADE @NonNull
    public ResponseEntity<Void> deleteUsuario(@PathVariable @NonNull Long id) {
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody @NonNull LoginRequest loginRequest) {
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getNombreUsuario(), 
                loginRequest.getContraseña()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 2. ¡AQUÍ ESTÁ LA CORRECCIÓN!
        // Validamos que el nombre de usuario no sea nulo antes de pasarlo.
        String username = Objects.requireNonNull(authentication.getName(), "El nombre de usuario no puede ser nulo después del login");
        
        Optional<Usuario> usuario = usuarioService.findByUsername(username);
        
        return usuario.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.status(401).build());
    }
    
    // ... (Clase interna LoginRequest no cambia) ...
    public static class LoginRequest {
        private String nombreUsuario;
        private String contraseña;
        public String getNombreUsuario() { return nombreUsuario; }
        public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
        public String getContraseña() { return contraseña; }
        public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    }
}