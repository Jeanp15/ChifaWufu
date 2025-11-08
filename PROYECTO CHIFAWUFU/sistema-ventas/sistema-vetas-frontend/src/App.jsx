import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../../../sistema-vetas-frontend/src/context/AuthContext.jsx';
// Necesitarás crear estas páginas en la carpeta /pages
import LoginPage from '../../../sistema-vetas-frontend/src/pages/LoginPage';
import AdminDashboard from '../../../sistema-vetas-frontend/src/pages/AdminDashboard';
import CocinaDashboard from '../../../sistema-vetas-frontend/src/pages/CocinaDashboard';
import CajeroDashboard from '../../../sistema-vetas-frontend/src/pages/CajeroDashboard';

// Este es el componente MÁGICO que protege tus rutas
const ProtectedRuta = ({ rolesPermitidos, children }) => {
    const { usuario } = useAuth();

    if (!usuario) {
        // 1. Si no hay usuario, ¡fuera!
        return <Navigate to="/login" replace />;
    }

    if (!rolesPermitidos.includes(usuario.rol)) {
        // 2. Si el ROL no coincide, ¡fuera!
        return <Navigate to="/no-autorizado" replace />; // (Crea esta página)
    }

    // 3. Si todo está bien, muestra la página
    return children;
};

function App() {
    return (
        <Routes>
            {/* Rutas Públicas */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/no-autorizado" element={<h1>No tienes permiso</h1>} />

            {/* Rutas Protegidas por Rol */}
            
            {/* Solo Admin */}
            <Route path="/admin/*" element={
                <ProtectedRuta rolesPermitidos={['Administrador']}>
                    <AdminDashboard />
                </ProtectedRuta>
            } />
            
            {/* Solo Cocinero (y Admin) */}
            <Route path="/cocina" element={
                <ProtectedRuta rolesPermitidos={['Cocinero', 'Administrador']}>
                    <CocinaDashboard />
                </ProtectedRuta>
            } />

            {/* Cajero (y Admin) */}
            <Route path="/caja" element={
                <ProtectedRuta rolesPermitidos={['Cajero', 'Administrador']}>
                    <CajeroDashboard />
                </ProtectedRuta>
            } />

            {/* Mozo (y Admin) */}
            {/* <Route path="/mozo" ... /> */}

            {/* Ruta por defecto */}
            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
}

export default App;