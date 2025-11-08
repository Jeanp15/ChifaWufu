import React, { useState } from 'react';
import { ventaService } from '../services/apiService'; // ¡Importamos el nuevo servicio!

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
        width: '90%', margin: '20px auto', padding: '20px',
        border: `1px solid ${colores.grisClaro}`, borderRadius: '8px',
        backgroundColor: colores.blanco, color: colores.texto,
        boxShadow: '0 4px 12px rgba(0,0,0,0.05)'
    },
    h2: {
        color: colores.rojoChifa,
        borderBottom: `3px solid ${colores.dorado}`,
        paddingBottom: '10px'
    },
    controles: {
        display: 'flex', gap: '10px', marginBottom: '20px',
        paddingBottom: '20px', borderBottom: `2px solid ${colores.rojoChifa}`,
        alignItems: 'center'
    },
    input: { padding: '10px', border: `1px solid ${colores.grisClaro}`, borderRadius: '4px', fontSize: '1em' },
    button: {
        padding: '10px 15px', backgroundColor: colores.rojoChifa,
        color: 'white', border: 'none', cursor: 'pointer',
        borderRadius: '4px', fontSize: '1em', fontWeight: 'bold',
    },
    // Estilos para el reporte (resumen)
    resumenContainer: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
        gap: '20px',
        marginTop: '20px'
    },
    resumenCard: {
        backgroundColor: colores.fondo,
        padding: '20px',
        borderRadius: '8px',
        borderLeft: `5px solid ${colores.rojoChifa}`
    },
    resumenTitulo: { margin: '0 0 10px 0', fontSize: '1.1em', color: colores.texto, fontWeight: '600' },
    resumenValor: { margin: '0', fontSize: '2em', color: colores.rojoChifa, fontWeight: 'bold' }
};

// Función para obtener la fecha de hoy en formato YYYY-MM-DD
const getHoy = () => new Date().toISOString().split('T')[0];

// --- Componente ---
export default function GestionReportes() {
    const [fecha, setFecha] = useState(getHoy());
    const [cierreCaja, setCierreCaja] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleGenerarReporte = async () => {
        if (!fecha) {
            setError('Por favor, selecciona una fecha.');
            return;
        }
        setError('');
        setLoading(true);
        setCierreCaja(null);
        try {
            const response = await ventaService.getCierreCaja(fecha);
            setCierreCaja(response.data);
            setLoading(false);
        } catch (err) {
            console.error("Error al generar cierre de caja:", err);
            setError('Error al cargar el reporte.');
            setLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <h2 style={styles.h2}>Reportes y Cierre de Caja (CU10)</h2>
            
            <div style={styles.controles}>
                <label>Selecciona una fecha:</label>
                <input 
                    style={styles.input} 
                    type="date" 
                    value={fecha} 
                    onChange={(e) => setFecha(e.target.value)} 
                />
                <button style={styles.button} onClick={handleGenerarReporte} disabled={loading}>
                    {loading ? 'Generando...' : 'Generar Cierre de Caja'}
                </button>
            </div>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            {/* Aquí mostramos los resultados del CierreCajaDTO */}
            {cierreCaja && (
                <div>
                    <h3>Resumen del Día: {fecha}</h3>
                    <div style={styles.resumenContainer}>
                        <div style={styles.resumenCard}>
                            <h4 style={styles.resumenTitulo}>Total General Vendido</h4>
                            <p style={styles.resumenValor}>S/ {cierreCaja.totalGeneral.toFixed(2)}</p>
                        </div>
                        <div style={styles.resumenCard}>
                            <h4 style={styles.resumenTitulo}>Total Transacciones</h4>
                            <p style={styles.resumenValor}>{cierreCaja.totalTransacciones}</p>
                        </div>
                        <div style={styles.resumenCard}>
                            <h4 style={styles.resumenTitulo}>Total en Efectivo</h4>
                            <p style={styles.resumenValor}>S/ {cierreCaja.totalEfectivo.toFixed(2)}</p>
                        </div>
                        <div style={styles.resumenCard}>
                            <h4 style={styles.resumenTitulo}>Total en Tarjeta</h4>
                            <p style={styles.resumenValor}>S/ {cierreCaja.totalTarjeta.toFixed(2)}</p>
                        </div>
                        <div style={styles.resumenCard}>
                            <h4 style={styles.resumenTitulo}>Total en Delivery</h4>
                            <p style={styles.resumenValor}>S/ {cierreCaja.totalDelivery.toFixed(2)}</p>
                        </div>
                         <div style={styles.resumenCard}>
                            <h4 style={styles.resumenTitulo}>Total en Salón</h4>
                            <p style={styles.resumenValor}>S/ {cierreCaja.totalSalon.toFixed(2)}</p>
                        </div>
                    </div>
                    {/* Aquí podrías renderizar la tabla con 'cierreCaja.ventasDelDia' */}
                </div>
            )}
        </div>
    );
}