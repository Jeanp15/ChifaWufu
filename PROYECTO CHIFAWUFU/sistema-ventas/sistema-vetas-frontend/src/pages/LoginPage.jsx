import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// --- RUTAS CORREGIDAS ---
import { useAuth } from '../context/AuthContext.jsx'; 
import { authService } from '../services/apiService.js'; // <- Usamos .js
// -------------------------

// Estilos simples para el formulario (opcional)
const styles = {
    container: { width: '300px', margin: '100px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px' },
    formGroup: { marginBottom: '15px' },
    label: { display: 'block', marginBottom: '5px' },
    input: { width: '100%', padding: '8px', boxSizing: 'border-box' },
    button: { width: '100%', padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none', cursor: 'pointer' },
    error: { color: 'red', marginTop: '10px' }
};

export default function LoginPage() {
    const [nombreUsuario, setNombreUsuario] = useState('');
    const [contraseña, setContraseña] = useState('');
    const [error, setError] = useState('');

    const navigate = useNavigate();
    const { login } = useAuth(); // Función que viene de nuestro AuthContext

    // Función que se llama al enviar el formulario
    const handleSubmit = async (e) => {
        e.preventDefault(); 
        setError(''); 

        try {
            // 3. ¡Aquí ocurre la magia! Llamamos a la API
            const usuarioData = await authService.login(nombreUsuario, contraseña);

            // 4. Si la API nos devuelve al usuario, lo guardamos en el "corazón"
            login(usuarioData);

            // 5. Redirigimos al usuario según su ROL
            switch (usuarioData.rol) {
                case 'Administrador':
                    navigate('/admin');
                    break;
                case 'Cajero':
                    navigate('/caja');
                    break;
                case 'Cocinero':
                    navigate('/cocina');
                    break;
                case 'Mozo':
                    navigate('/mozo'); // (Añade esta ruta en App.jsx si la necesitas)
                    break;
                default:
                    navigate('/login'); 
            }

        } catch (err) {
            // 6. Si el backend da un error (401 No Autorizado), lo mostramos
            console.error("Error en el login:", err);
            setError('Usuario o contraseña incorrectos.');
        }
    };

    return (
        <div style={styles.container}>
            <h2>Iniciar Sesión</h2>
            <form onSubmit={handleSubmit}>
                <div style={styles.formGroup}>
                    <label style={styles.label}>Usuario:</label>
                    <input
                        style={styles.input}
                        type="text"
                        value={nombreUsuario}
                        onChange={(e) => setNombreUsuario(e.target.value)}
                        required
                    />
                </div>
                <div style={styles.formGroup}>
                    <label style={styles.label}>Contraseña:</label>
                    <input
                        style={styles.input}
                        type="password"
                        value={contraseña}
                        onChange={(e) => setContraseña(e.target.value)}
                        required
                    />
                </div>
                <button style={styles.button} type="submit">Ingresar</button>
                
                {error && <p style={styles.error}>{error}</p>}
            </form>
        </div>
    );
}