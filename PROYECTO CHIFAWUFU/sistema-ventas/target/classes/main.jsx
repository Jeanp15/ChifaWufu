import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';
import { AuthProvider } from './context/AuthContext.jsx'; // 1. Importa
import { BrowserRouter } from 'react-router-dom'; // 2. Importa

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <BrowserRouter> {/* 3. Añade el Router */}
            <AuthProvider> {/* 4. Envuelve tu App */}
                <App />
            </AuthProvider>
        </BrowserRouter>
    </React.StrictMode>,
);