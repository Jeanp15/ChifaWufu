import React, { useState, useEffect } from 'react';
import { clienteService } from '../services/apiService'; // ¡Importamos el nuevo servicio!

// --- Paleta de Colores Chifa Wu Fu ---
const colores = {
    rojoChifa: '#D92323', // Rojo principal
    dorado: '#F2B705',    // Acento dorado
    fondo: '#f4f4f4',     // Fondo claro
    texto: '#333',
    grisClaro: '#ddd',
    blanco: '#fff',
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
        boxShadow: '0 4px 12px rgba(0,0,0,0.05)'
    },
    h2: {
        color: colores.rojoChifa,
        borderBottom: `3px solid ${colores.dorado}`,
        paddingBottom: '10px'
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
        borderCollapse: 'collapse',
        marginTop: '20px'
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
        color: 'white',
        fontWeight: 'bold'
    }
};

// --- Componente ---
export default function GestionClientes() {
    const [clientes, setClientes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Estado para el formulario (basado en Cliente.java)
    const [nombre, setNombre] = useState('');
    const [dni, setDni] = useState('');
    const [telefono, setTelefono] = useState('');
    const [direccion, setDireccion] = useState('');
    const [correo, setCorreo] = useState('');

    // Cargar clientes al iniciar
    useEffect(() => {
        cargarClientes();
    }, []);

    const cargarClientes = async () => {
        try {
            setLoading(true);
            const response = await clienteService.getAllClientes();
            setClientes(response.data);
            setLoading(false);
        } catch (err) {
            console.error("Error al cargar clientes:", err);
            setError('Error al cargar clientes.');
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const nuevoCliente = { nombre, dni, telefono, direccion, correo };
            await clienteService.crearCliente(nuevoCliente);
            
            // Limpiar formulario y recargar
            setNombre('');
            setDni('');
            setTelefono('');
            setDireccion('');
            setCorreo('');
            cargarClientes();

        } catch (err) {
            console.error("Error al crear cliente:", err);
            setError('Error al crear el cliente.');
        }
    };

    if (loading) return <p>Cargando clientes...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div style={styles.container}>
            <h2 style={styles.h2}>Gestión de Clientes</h2>
            
            <form onSubmit={handleSubmit} style={styles.form}>
                <input style={styles.input} type="text" placeholder="Nombre completo" value={nombre} onChange={(e) => setNombre(e.target.value)} required />
                <input style={styles.input} type="text" placeholder="DNI / RUC" value={dni} onChange={(e) => setDni(e.target.value)} />
                <input style={styles.input} type="text" placeholder="Teléfono" value={telefono} onChange={(e) => setTelefono(e.target.value)} />
                <input style={styles.input} type="text" placeholder="Dirección" value={direccion} onChange={(e) => setDireccion(e.target.value)} />
                <input style={styles.input} type="email" placeholder="Correo (opcional)" value={correo} onChange={(e) => setCorreo(e.target.value)} />
                <button style={styles.button} type="submit">Crear Cliente</button>
            </form>

            <table style={styles.table}>
                <thead>
                    <tr>
                        <th style={styles.th}>Nombre</th>
                        <th style={styles.th}>DNI/RUC</th>
                        <th style={styles.th}>Teléfono</th>
                        <th style={styles.th}>Dirección</th>
                        <th style={styles.th}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {clientes.map(cliente => (
                        <tr key={cliente.idCliente}>
                            <td style={styles.td}>{cliente.nombre}</td>
                            <td style={styles.td}>{cliente.dni}</td>
                            <td style={styles.td}>{cliente.telefono}</td>
                            <td style={styles.td}>{cliente.direccion}</td>
                            <td style={styles.td}>
                                <button style={{ ...styles.actionButton, backgroundColor: colores.dorado }}>Editar</button>
                                <button style={{ ...styles.actionButton, backgroundColor: colores.rojoChifa }}>Borrar</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}