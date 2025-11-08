import React, { useState, useEffect } from 'react';
import { productoService } from '../services/apiService';

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
    },
    statusBadge: {
        padding: '3px 8px', 
        borderRadius: '12px', 
        color: 'white',
        fontSize: '0.9em',
        fontWeight: 'bold'
    }
};

// --- Componente ---
export default function GestionProductos() {
    const [productos, setProductos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Estado para el formulario
    const [nombre, setNombre] = useState('');
    const [descripcion, setDescripcion] = useState('');
    const [precio, setPrecio] = useState('');
    const [categoria, setCategoria] = useState('Platos Principales');
    const [stock, setStock] = useState(0);

    // Cargar productos al iniciar
    useEffect(() => {
        cargarProductos();
    }, []);

    const cargarProductos = async () => {
        try {
            setLoading(true);
            const response = await productoService.getAllProductos();
            setProductos(response.data);
            setLoading(false);
        } catch (err) {
            console.error("Error al cargar productos:", err);
            setError('Error al cargar productos.');
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const nuevoProducto = { 
                nombre, 
                descripcion, 
                precio: parseFloat(precio), 
                categoria, 
                stock: parseInt(stock, 10),
                activo: true // Los productos se crean como activos por defecto
            };
            await productoService.crearProducto(nuevoProducto);
            
            // Limpiar formulario y recargar
            setNombre('');
            setDescripcion('');
            setPrecio('');
            setStock(0);
            cargarProductos();

        } catch (err) {
            console.error("Error al crear producto:", err);
            setError('Error al crear el producto.');
        }
    };

    if (loading) return <p>Cargando productos...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div style={styles.container}>
            <h2 style={styles.h2}>Gestión de Productos (Menú)</h2>
            
            <form onSubmit={handleSubmit} style={styles.form}>
                <input style={styles.input} type="text" placeholder="Nombre (Ej: Arroz Chaufa)" value={nombre} onChange={(e) => setNombre(e.target.value)} required />
                <input style={styles.input} type="text" placeholder="Descripción" value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
                <input style={styles.input} type="number" placeholder="Precio (Ej: 15.50)" value={precio} onChange={(e) => setPrecio(e.target.value)} required step="0.01" />
                <select style={styles.input} value={categoria} onChange={(e) => setCategoria(e.target.value)}>
                    <option value="Platos Principales">Platos Principales</option>
                    <option value="Sopas">Sopas</option>
                    <option value="Bebidas">Bebidas</option>
                    <option value="Postres">Postres</option>
                </select>
                <input style={styles.input} type="number" placeholder="Stock" value={stock} onChange={(e) => setStock(e.target.value)} required />
                <button style={styles.button} type="submit">Crear Producto</button>
            </form>

            <table style={styles.table}>
                <thead>
                    <tr>
                        <th style={styles.th}>Nombre</th>
                        <th style={styles.th}>Precio</th>
                        <th style={styles.th}>Categoría</th>
                        <th style={styles.th}>Stock</th>
                        <th style={styles.th}>Activo</th>
                        <th style={styles.th}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {productos.map(prod => (
                        <tr key={prod.idProducto}>
                            <td style={styles.td}>{prod.nombre}</td>
                            <td style={styles.td}>S/ {prod.precio.toFixed(2)}</td>
                            <td style={styles.td}>{prod.categoria}</td>
                            <td style={styles.td}>{prod.stock}</td>
                            <td style={styles.td}>
                                <span style={{ 
                                    ...styles.statusBadge,
                                    backgroundColor: prod.activo ? colores.verde : colores.grisOscuro 
                                }}>
                                    {prod.activo ? 'Sí' : 'No'}
                                </span>
                            </td>
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