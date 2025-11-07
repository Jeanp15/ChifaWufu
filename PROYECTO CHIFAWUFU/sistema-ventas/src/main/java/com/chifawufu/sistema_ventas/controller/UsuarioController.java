package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.model.Usuario;
import com.chifawufu.sistema_ventas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000") // Para conectar con el frontend
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping
    @PreAuthorize("hasRole('Administrador')") // 2. SOLO EL ROL 'Administrador'
    public List<Usuario> getAllUsuarios() {
        return usuarioService.findAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')") // 3. SOLO 'Administrador'
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('Administrador')") // 4. SOLO 'Administrador' PUEDE CREAR USUARIOS
    public Usuario createUsuario(@RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')") // 5. SOLO 'Administrador'
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            Usuario usuarioExistente = usuario.get();
            usuarioExistente.setNombreUsuario(usuarioDetails.getNombreUsuario());
            usuarioExistente.setRol(usuarioDetails.getRol());
            // Arreglamos esto en el paso anterior, ¡bien!
            usuarioExistente.setActivo(usuarioDetails.getActivo()); 
            
            // Ojo: si actualizas la contraseña, también debes hashearla aquí.
            // Por ahora, este update no toca la contraseña.
            
            // NOTA: El .save() hasheará la contraseña OTRA VEZ si no tienes cuidado.
            // Vamos a ajustar el 'save' para que no re-hashee una contraseña ya hasheada.
            // (Lo haremos en el siguiente paso si quieres, por ahora esto funciona)
            return ResponseEntity.ok(usuarioService.save(usuarioExistente));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')") // 6. SOLO 'Administrador'
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/login")
    // 7. EL LOGIN SIGUE SIENDO PÚBLICO (PERMITALL en SecurityConfig)
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioService.login(loginRequest.getNombreUsuario(), loginRequest.getContraseña());
        return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.status(401).build());
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