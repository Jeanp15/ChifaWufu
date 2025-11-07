import React, { createContext, useState, useContext } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [usuario, setUsuario] = useState(null);

    // Guardamos el usuario que nos devuelve el login
    const login = (userData) => {
        setUsuario(userData);
        // Opcional: podrías guardar esto en localStorage
    };

    const logout = () => {
        setUsuario(null);
        // Opcional: limpiar localStorage y llamar a /logout del backend
    };

    // Compartimos el usuario y las funciones
    return (
        <AuthContext.Provider value={{ usuario, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// Este es el "hook" que usarán tus componentes
export const useAuth = () => {
    return useContext(AuthContext);
};