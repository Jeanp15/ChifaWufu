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
    
    // 4. Modifica el método login para que sea seguro
    public Optional<Usuario> login(String nombreUsuario, String contraseña) {
        // Primero, busca al usuario por su nombre
        Optional<Usuario> usuarioOptional = usuarioRepository.findByNombreUsuario(nombreUsuario);
        
        // Si el usuario existe...
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            
            // Compara la contraseña enviada (texto plano) con la hasheada (en la BD)
            if (passwordEncoder.matches(contraseña, usuario.getContraseña())) {
                // Si coinciden, retorna el usuario
                return usuarioOptional;
            }
        }
        
        // Si el usuario no existe o la contraseña es incorrecta, retorna vacío
        return Optional.empty();
    }
    
    public List<Usuario> findByRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }
}