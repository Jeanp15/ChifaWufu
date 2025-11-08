import React, { useState } from 'react';
import GestionUsuarios from '../components/GestionUsuarios.jsx';
import GestionProductos from '../components/GestionProductos.jsx';
import GestionClientes from '../components/GestionClientes.jsx';
import GestionReportes from '../components/GestionReportes.jsx'; // 1. IMPORTA EL NUEVO PANEL
import { useAuth } from '../context/AuthContext.jsx';
import { useNavigate } from 'react-router-dom';

// --- Estilos con la Paleta ---
const colores = {
    rojoChifa: '#D92323',
    dorado: '#F2B705',
    fondoHeader: '#333',
    textoHeader: '#fff',
    fondoApp: '#f4f4f4'
};

const styles = {
    dashboard: { width: '100%', minHeight: '100vh', backgroundColor: colores.fondoApp },
    header: { 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        padding: '10px 40px', 
        backgroundColor: colores.fondoHeader, 
        color: colores.textoHeader 
    },
    nav: {
        display: 'flex',
        justifyContent: 'center',
        gap: '10px',
        backgroundColor: '#444',
        padding: '10px'
    },
    navButton: {
        padding: '10px 20px',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        backgroundColor: colores.dorado,
        color: colores.fondoHeader,
        fontWeight: 'bold',
        fontSize: '1em'
    },
    logoutButton: {
        padding: '8px 12px',
        backgroundColor: colores.rojoChifa,
        color: 'white',
        border: 'none',
        cursor: 'pointer',
        borderRadius: '4px'
    }
};

export default function AdminDashboard() {
    // Estado para saber qué panel mostrar
    const [vistaActual, setVistaActual] = useState('reportes'); // <-- Puesto por defecto
    
    const { logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout(); // Limpia el usuario del AuthContext
        navigate('/login'); // Redirige al login
    };

    // Función para renderizar el panel correcto
    const renderizarVista = () => {
        if (vistaActual === 'productos') {
            return <GestionProductos />;
        }
        if (vistaActual === 'usuarios') {
            return <GestionUsuarios />;
        }
        if (vistaActual === 'clientes') {
            return <GestionClientes />;
        }
        // 2. AÑADE LA VISTA DE REPORTES
        if (vistaActual === 'reportes') {
            return <GestionReportes />;
        }
        return null;
    };

    return (
        <div style={styles.dashboard}>
            {/* Header / Barra de Título */}
            <header style={styles.header}>
                <h1>Panel de Administrador (Chifa Wu Fu)</h1>
                <button onClick={handleLogout} style={styles.logoutButton}>
                    Cerrar Sesión
                </button>
            </header>

            {/* Menú de Navegación del Panel */}
            <nav style={styles.nav}>
                {/* 3. AÑADE EL BOTÓN DE REPORTES */}
                <button style={styles.navButton} onClick={() => setVistaActual('reportes')}>
                    Reportes y Caja
                </button>
                <button style={styles.navButton} onClick={() => setVistaActual('productos')}>
                    Gestionar Productos
                </button>
                <button style={styles.navButton} onClick={() => setVistaActual('usuarios')}>
                    Gestionar Usuarios
                </button>
                <button style={styles.navButton} onClick={() => setVistaActual('clientes')}>
                    Gestionar Clientes
                </button>
            </nav>

            {/* Contenido Principal */}
            <main>
                {renderizarVista()}
            </main>
        </div>
    );
}