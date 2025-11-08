package com.chifawufu.sistema_ventas;

import com.chifawufu.sistema_ventas.model.Usuario;
import com.chifawufu.sistema_ventas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // El hasher que ya creamos

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Buscamos si ya existe algún administrador
        //    Usamos el método que ya existe en tu UsuarioRepository
        if (usuarioRepository.findByRol("Administrador").isEmpty()) {
            
            System.out.println("--- NO SE ENCONTRARON ADMINS, CREANDO USUARIO ADMIN POR DEFECTO ---");

            // 2. Si no existe, creamos uno nuevo
            //    Usamos el constructor de tu clase Usuario
            Usuario admin = new Usuario(
                "admin", // nombreUsuario
                "admin123", // contraseña (la hashearemos ahora)
                "Administrador" // rol
            );
            admin.setActivo(true);
            
            // 3. Hasheamos la contraseña antes de guardarla
            admin.setContraseña(passwordEncoder.encode(admin.getContraseña()));

            // 4. Guardamos el nuevo admin
            usuarioRepository.save(admin);
            
            System.out.println("--- USUARIO 'admin' CREADO CON CONTRASEÑA 'admin123' ---");
        } else {
            System.out.println("--- El usuario Administrador ya existe. No se necesita crear. ---");
        }
    }
}