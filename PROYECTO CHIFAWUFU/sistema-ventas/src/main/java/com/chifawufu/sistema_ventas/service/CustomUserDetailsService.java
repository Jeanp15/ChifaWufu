package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.model.Usuario;
import com.chifawufu.sistema_ventas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        // 1. Buscamos al usuario en tu repositorio
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(nombreUsuario);

        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario);
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Creamos el UserDetails que Spring Security entiende
        //    Usamos el constructor que incluye roles/autoridades.
        return User.withUsername(usuario.getNombreUsuario())
                .password(usuario.getContraseña()) // La contraseña ya está hasheada
                .roles(usuario.getRol()) // ¡Aquí es donde pasamos el rol!
                .build();
    }
}