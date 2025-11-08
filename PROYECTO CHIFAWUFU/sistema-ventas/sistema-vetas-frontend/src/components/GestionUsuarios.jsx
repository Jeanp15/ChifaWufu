import React, { useState, useEffect } from 'react';
// Importamos el servicio de usuarios que ya creamos
import { usuarioService } from '../services/apiService.js'; // (asegúrate que tu apiService sea .js)

// --- Paleta de Colores Chifa Wu Fu ---
const colores = {
    rojoChifa: '#D92323', // Rojo principal
    dorado: '#F2B705',    // Acento dorado
    fondo: '#f4f4f4',     // Fondo claro
    texto: '#333',
    grisClaro: '#ddd',
    blanco: '#fff',
    verde: '#28a745', // Para botones de "Activo"
    grisOscuro: '#555' // Para botones de "Inactivo"
};

// --- Estilos ---
const styles = {
    container: {
        width: '90%',
        margin: '20px auto',
        padding: '20px',
        border: `1px solid ${colores.grisClaro}`,
        borderRadius: '8px',
        backgroundColor: colores.blanco,
        color: colores.texto,
    },
    form: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '15px',
        marginBottom: '20px',
        paddingBottom: '20px',
        borderBottom: `2px solid ${colores.rojoChifa}`
    },
    input: {
        padding: '10px',
        border: `1px solid ${colores.grisClaro}`,
        borderRadius: '4px',
        fontSize: '1em'
    },
    button: {
        padding: '10px 15px',
        backgroundColor: colores.rojoChifa,
        color: 'white',
        border: 'none',
        cursor: 'pointer',
        borderRadius: '4px',
        fontSize: '1em',
        fontWeight: 'bold',
        gridColumn: '1 / -1' // El botón ocupa todo el ancho
    },
    table: {
        width: '100%',
        borderCollapse: 'collapse'
    },
    th: {
        backgroundColor: colores.rojoChifa,
        color: colores.blanco,
        padding: '12px',
        border: `1px solid ${colores.grisClaro}`
    },
    td: {
        padding: '10px',
        border: `1px solid ${colores.grisClaro}`,
        textAlign: 'left'
    },
    actionButton: {
        padding: '5px 10px',
        marginRight: '5px',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        color: 'white'
    }
};

// --- Componente ---
export default function GestionUsuarios() {
    const [usuarios, setUsuarios] = useState([]); // Almacena la lista de usuarios
    const [loading, setLoading] = useState(true); // Para saber si está cargando
    const [error, setError] = useState('');

    // Estado para el formulario de nuevo usuario
    const [nombreUsuario, setNombreUsuario] = useState('');
    const [contraseña, setContraseña] = useState('');
    const [rol, setRol] = useState('Cajero'); // Rol por defecto

    // useEffect se ejecuta cuando el componente se carga
    useEffect(() => {
        cargarUsuarios();
    }, []);

    // Función para cargar los usuarios desde la API
    const cargarUsuarios = async () => {
        try {
            setLoading(true);
            const response = await usuarioService.getAllUsuarios();
            setUsuarios(response.data);
            setLoading(false);
        } catch (err) {
            console.error("Error al cargar usuarios:", err);
            setError('Error al cargar usuarios. ¿Estás logueado como Admin?');
            setLoading(false);
        }
    };

    // Función para manejar la creación de un nuevo usuario
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const nuevoUsuario = { nombreUsuario, contraseña, rol, activo: true };
            await usuarioService.crearUsuario(nuevoUsuario);
            
            // Limpiar formulario y recargar la lista
            setNombreUsuario('');
            setContraseña('');
            setRol('Cajero');
            cargarUsuarios(); // Recargamos la lista

        } catch (err) {
            console.error("Error al crear usuario:", err);
            setError('Error al crear el usuario.');
        }
    };

    if (loading) return <p>Cargando usuarios...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div style={styles.container}>
            <h2>Gestión de Usuarios</h2>
            
            {/* Formulario para crear nuevo usuario */}
            <form onSubmit={handleSubmit} style={styles.form}>
                <input
                    style={styles.input}
                    type="text"
                    placeholder="Nombre de usuario"
                    value={nombreUsuario}
                    onChange={(e) => setNombreUsuario(e.target.value)}
                    required
                />
                <input
                    style={styles.input}
                    type="password"
                    placeholder="Contraseña"
                    value={contraseña}
                    onChange={(e) => setContraseña(e.target.value)}
                    required
                />
                <select style={styles.input} value={rol} onChange={(e) => setRol(e.target.value)}>
                    <option value="Cajero">Cajero</option>
                    <option value="Mozo">Mozo</option>
                    <option value="Cocinero">Cocinero</option>
                    <option value="Administrador">Administrador</option>
                </select>
                <button style={styles.button} type="submit">Crear Usuario</button>
            </form>

            {/* Tabla de usuarios existentes */}
            <table style={styles.table}>
                <thead>
                    <tr>
                        <th style={styles.th}>ID</th>
                        <th style={styles.th}>Nombre Usuario</th>
                        <th style={styles.th}>Rol</th>
                        <th style={styles.th}>Activo</th>
                        <th style={styles.th}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {usuarios.map(usuario => (
                        <tr key={usuario.idUsuario}>
                            <td style={styles.td}>{usuario.idUsuario}</td>
                            <td style={styles.td}>{usuario.nombreUsuario}</td>
                            <td style={styles.td}>{usuario.rol}</td>
                            <td style={styles.td}>{usuario.activo ? 'Sí' : 'No'}</td>
                            <td style={styles.td}>
                                {/* Aquí irían los botones de Editar y Borrar */}
                                <button>Editar</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}