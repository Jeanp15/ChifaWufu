import axios from 'axios';

// Creamos una instancia de Axios
const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api', // La base de tu API
    withCredentials: true // ¡MUY IMPORTANTE! Para que envíe la cookie de sesión
});

// Este es el servicio de autenticación
export const authService = {
    login: async (nombreUsuario, contraseña) => {
        // Llama al endpoint que arreglamos
        const response = await apiClient.post('/usuarios/login', {
            nombreUsuario,
            contraseña
        });
        return response.data; // Devuelve el objeto Usuario
    },
    // logout: () => apiClient.post('/logout'), // (Si configuras el logout)
};

// Aquí añadirías los otros servicios...
export const productoService = {
    getProductosActivos: () => apiClient.get('/productos/activos'),
};

export const pedidoService = {
    getPedidosPendientes: () => apiClient.get('/pedidos/pendientes'),
    crearPedido: (pedidoData) => apiClient.post('/pedidos', pedidoData),
};

// ... etc. para Venta, Cliente, etc.