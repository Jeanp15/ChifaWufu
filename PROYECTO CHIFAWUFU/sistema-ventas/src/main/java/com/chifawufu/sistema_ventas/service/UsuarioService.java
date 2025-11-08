package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.model.Usuario;
import com.chifawufu.sistema_ventas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.lang.NonNull; // 1. IMPORTAMOS @NonNull

import java.util.Optional;
import java.util.List;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    // 2. AÑADIMOS @NonNull
    public Optional<Usuario> findById(@NonNull Long id) {
        return usuarioRepository.findById(id);
    }
    
    // 3. MÉTODO 'save' RENOMBRADO A 'crearUsuario'
    // Este método es solo para CREAR usuarios y SIEMPRE hashea la contraseña
    public Usuario crearUsuario(@NonNull Usuario usuario) {
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        return usuarioRepository.save(usuario);
    }

    // 4. NUEVO MÉTODO PARA ACTUALIZAR (corrige el error de lógica)
    // Este método solo guarda cambios (ej. rol, activo) y NO TOCA la contraseña
    public Usuario actualizarUsuario(@NonNull Usuario usuario) {
        // No hasheamos la contraseña, solo guardamos el objeto
        return usuarioRepository.save(usuario);
    }
    
    // 5. AÑADIMOS @NonNull
    public void deleteById(@NonNull Long id) {
        usuarioRepository.deleteById(id);
    }
    
    // 6. AÑADIMOS @NonNull
    public Optional<Usuario> findByUsername(@NonNull String username) {
        return usuarioRepository.findByNombreUsuario(username);
    }
    
    // 7. AÑADIMOS @NonNull
    public List<Usuario> findByRol(@NonNull String rol) {
        return usuarioRepository.findByRol(rol);
    }
}