import React, { useState, useEffect } from 'react';
import { pedidoService } from '../services/apiService'; // Importamos el servicio
import { useAuth } from '../context/AuthContext.jsx';
import { useNavigate } from 'react-router-dom';

// --- Paleta de Colores Chifa Wu Fu ---
const colores = {
    rojoChifa: '#D92323',
    dorado: '#F2B705',
    fondoHeader: '#333',
    textoHeader: '#fff',
    fondoApp: '#f4f4f4',
    verde: '#28a745',
    blanco: '#fff',
    texto: '#333',
    grisClaro: '#ddd'
};

// --- Estilos ---
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
    logoutButton: {
        padding: '8px 12px',
        backgroundColor: colores.rojoChifa,
        color: 'white',
        border: 'none',
        cursor: 'pointer',
        borderRadius: '4px'
    },
    // Contenedor de las "comandas"
    comandasContainer: {
        display: 'flex',
        flexWrap: 'wrap',
        gap: '20px',
        padding: '20px'
    },
    comandaCard: {
        backgroundColor: colores.blanco,
        border: `1px solid ${colores.grisClaro}`,
        borderRadius: '8px',
        boxShadow: '0 4px 12px rgba(0,0,0,0.05)',
        color: colores.texto,
        width: '300px',
        overflow: 'hidden' // Para que el header no se salga
    },
    comandaHeader: {
        backgroundColor: colores.dorado,
        color: colores.fondoHeader,
        padding: '10px 15px',
        fontSize: '1.2em',
        fontWeight: 'bold'
    },
    comandaBody: {
        padding: '15px'
    },
    listaItems: {
        listStyle: 'none',
        padding: '0',
        margin: '0 0 15px 0'
    },
    item: {
        fontSize: '1.1em',
        padding: '5px 0',
        borderBottom: `1px dashed ${colores.grisClaro}`
    },
    itemCantidad: {
        fontWeight: 'bold',
        color: colores.rojoChifa,
        marginRight: '10px'
    },
    botonListo: {
        width: '100%',
        padding: '12px',
        fontSize: '1.1em',
        fontWeight: 'bold',
        backgroundColor: colores.verde,
        color: 'white',
        border: 'none',
        cursor: 'pointer'
    }
};

// --- Componente ---
export default function CocinaDashboard() {
    const [pedidos, setPedidos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const { logout } = useAuth();
    const navigate = useNavigate();

    // Cargar pedidos al iniciar
    useEffect(() => {
        cargarPedidosPendientes();

        // Opcional: Recargar los pedidos cada 30 segundos
        const intervalId = setInterval(cargarPedidosPendientes, 30000);
        return () => clearInterval(intervalId); // Limpiar el intervalo al salir
    }, []);

    const cargarPedidosPendientes = async () => {
        try {
            setLoading(true);
            const response = await pedidoService.getPedidosPendientes();
            setPedidos(response.data);
            setLoading(false);
        } catch (err) {
            console.error("Error al cargar pedidos:", err);
            setError('Error al cargar pedidos pendientes.');
            setLoading(false);
        }
    };

    const handleMarcarListo = async (idPedido) => {
        try {
            // Llamamos al nuevo método del servicio
            await pedidoService.actualizarEstadoPedido(idPedido, 'LISTO');
            // Recargamos la lista para que el pedido desaparezca
            cargarPedidosPendientes();
        } catch (err) {
            console.error("Error al actualizar estado:", err);
            setError('Error al marcar pedido como listo.');
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div style={styles.dashboard}>
            <header style={styles.header}>
                <h1>Panel de Cocina (Pedidos Pendientes)</h1>
                <button onClick={handleLogout} style={styles.logoutButton}>
                    Cerrar Sesión
                </button>
            </header>

            <main>
                {loading && <p>Cargando pedidos...</p>}
                {error && <p style={{ color: 'red' }}>{error}</p>}
                
                <div style={styles.comandasContainer}>
                    {pedidos.map(pedido => (
                        <div key={pedido.idPedido} style={styles.comandaCard}>
                            <div style={styles.comandaHeader}>
                                {pedido.tipo === 'SALON' ? `Mesa: ${pedido.mesa}` : 'Pedido DELIVERY'}
                            </div>
                            <div style={styles.comandaBody}>
                                <ul style={styles.listaItems}>
                                    {pedido.detalles.map(detalle => (
                                        <li key={detalle.idDetalle} style={styles.item}>
                                            <span style={styles.itemCantidad}>{detalle.cantidad}x</span>
                                            {detalle.producto.nombre}
                                        </li>
                                    ))}
                                </ul>
                                <button 
                                    style={styles.botonListo}
                                    onClick={() => handleMarcarListo(pedido.idPedido)}
                                >
                                    Marcar como LISTO
                                </button>
                            </div>
                        </div>
                    ))}
                </div>

                {!loading && pedidos.length === 0 && (
                    <h2 style={{ color: colores.texto, textAlign: 'center', marginTop: '50px' }}>
                        ¡No hay pedidos pendientes! 🎉
                    </h2>
                )}
            </main>
        </div>
    );
}