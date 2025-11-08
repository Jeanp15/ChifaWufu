import axios from 'axios';

// Creamos una instancia de Axios
const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api', // La base de tu API
    withCredentials: true // ¡MUY IMPORTANTE! Para que envíe la cookie de sesión
});

// --- Servicio de Autenticación ---
export const authService = {
    login: async (nombreUsuario, contraseña) => {
        const response = await apiClient.post('/usuarios/login', {
            nombreUsuario,
            contraseña
        });
        return response.data; // Devuelve el objeto Usuario
    },
    // logout: () => apiClient.post('/logout'),
};

// --- Servicio de Usuarios ---
export const usuarioService = {
    getAllUsuarios: () => {
        return apiClient.get('/usuarios');
    },
    crearUsuario: (usuarioData) => {
        return apiClient.post('/usuarios', usuarioData);
    },
    actualizarUsuario: (id, usuarioData) => {
        return apiClient.put(`/usuarios/${id}`, usuarioData);
    },
    eliminarUsuario: (id) => {
        return apiClient.delete(`/usuarios/${id}`);
    }
};

// --- Servicio de Productos ---
export const productoService = {
    getAllProductos: () => {
        return apiClient.get('/productos');
    },
    getProductosActivos: () => {
        return apiClient.get('/productos/activos');
    },
    crearProducto: (productoData) => {
        return apiClient.post('/productos', productoData);
    },
    actualizarProducto: (id, productoData) => {
        return apiClient.put(`/productos/${id}`, productoData);
    },
    eliminarProducto: (id) => {
        return apiClient.delete(`/productos/${id}`);
    }
};

// --- ¡SERVICIO DE PEDIDOS ACTUALIZADO! ---
export const pedidoService = {
    getPedidosPendientes: () => {
        // Llama al GET /api/pedidos/pendientes
        return apiClient.get('/pedidos/pendientes');
    },
    crearPedido: (pedidoData) => {
        return apiClient.post('/pedidos', pedidoData);
    },
    // ¡NUEVO MÉTODO! Llama al PUT /api/pedidos/{id}/estado
    actualizarEstadoPedido: (id, estado) => {
        return apiClient.put(`/pedidos/${id}/estado?estado=${estado}`);
    }
};

// --- Servicio de Clientes ---
export const clienteService = {
    getAllClientes: () => {
        return apiClient.get('/clientes');
    },
    crearCliente: (clienteData) => {
        return apiClient.post('/clientes', clienteData);
    },
    actualizarCliente: (id, clienteData) => {
        return apiClient.put(`/clientes/${id}`, clienteData);
    },
    eliminarCliente: (id) => {
        return apiClient.delete(`/clientes/${id}`);
    }
};

// --- Servicio de Ventas y Reportes ---
export const ventaService = {
    registrarVenta: (ventaData) => {
        return apiClient.post('/ventas', ventaData);
    },
    getReporteDia: (fecha) => {
        return apiClient.get(`/ventas/reporte/dia?fecha=${fecha}`);
    },
    getReporteRango: (inicio, fin) => {
        return apiClient.get(`/ventas/reporte/rango?inicio=${inicio}&fin=${fin}`);
    },
    getCierreCaja: (fecha) => {
        return apiClient.get(`/ventas/cierre-caja?fecha=${fecha}`);
    }
};