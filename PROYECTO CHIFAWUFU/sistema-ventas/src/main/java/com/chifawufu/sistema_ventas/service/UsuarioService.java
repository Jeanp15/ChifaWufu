package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.model.Usuario;
import com.chifawufu.sistema_ventas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 1. Importa estas dos clases nuevas
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import java.util.List;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // 2. Inyecta el "hasher" que creamos en el Paso 2
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    // 3. ¡MUY IMPORTANTE! Modifica el método save
    public Usuario save(Usuario usuario) {
        // Hashea la contraseña antes de guardarla
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        return usuarioRepository.save(usuario);
    }
    
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    // 4. EL MÉTODO 'login' SE ELIMINA Y SE REEMPLAZA POR ESTE:
    /**
     * Busca un usuario por su nombre de usuario (username).
     * Este método es usado por el 'UsuarioController' después
     * de que el 'AuthenticationManager' confirma la sesión.
     * @param username El nombre de usuario a buscar.
     * @return Un Optional con el Usuario (si se encuentra).
     */
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByNombreUsuario(username);
    }
    
    public List<Usuario> findByRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }
}