// ============================================
// CONFIGURACIÓN BASE
// ============================================
export const API_BASE_URL = 'http://localhost:8080/api';
export const STATIC_BASE_URL = 'http://localhost:8080';
export const MASCOTAS_FOTO_BASE = `${STATIC_BASE_URL}/img`;

// ============================================
// ENDPOINTS POR ENTIDAD
// ============================================
export const ENDPOINTS = {
  // Admin
  ADMINS: `${API_BASE_URL}/admins`,
  ADMINS_LOGIN: `${API_BASE_URL}/admins/login`,

  // Auth (si se usa)
  AUTH: `${API_BASE_URL}/auth`,
  AUTH_LOGIN: `${API_BASE_URL}/auth/login`,
  AUTH_REGISTER: `${API_BASE_URL}/auth/register`,

  // Cliente
  CLIENTES: `${API_BASE_URL}/clientes`,
  CLIENTES_LOGIN: `${API_BASE_URL}/clientes/login`,
  CLIENTES_MASCOTAS: (id: number) => `${API_BASE_URL}/clientes/${id}/mascotas`,

  // Veterinario
  VETERINARIOS: `${API_BASE_URL}/veterinarios`,
  VETERINARIOS_LOGIN: `${API_BASE_URL}/veterinarios/login`,
  VETERINARIOS_ESTADO: (id: number) => `${API_BASE_URL}/veterinarios/${id}/estado`,

  // Mascota
  MASCOTAS: `${API_BASE_URL}/mascotas`,
  MASCOTAS_DEACTIVATE: (id: number) => `${API_BASE_URL}/mascotas/${id}/deactivate`,
  MASCOTAS_BY_CLIENTE: (clienteId: number) => `${API_BASE_URL}/mascotas/cliente/${clienteId}`,
  MASCOTAS_FOTO: (id: number) => `${API_BASE_URL}/mascotas/${id}/foto`,

  // Droga
  DROGAS: `${API_BASE_URL}/drogas`,
  DROGAS_DISPONIBLES: `${API_BASE_URL}/drogas?disponibles=true`,
  DROGAS_DESCONTAR: (id: number) => `${API_BASE_URL}/drogas/${id}/descontar`,

  // Tratamiento
  TRATAMIENTOS: `${API_BASE_URL}/tratamientos`,
  TRATAMIENTOS_PROGRAMADOS: `${API_BASE_URL}/tratamientos?programados=true`,
  TRATAMIENTOS_BY_MASCOTA: (mascotaId: number) => `${API_BASE_URL}/tratamientos/mascota/${mascotaId}`,
  TRATAMIENTOS_BY_VETERINARIO: (veterinarioId: number) => `${API_BASE_URL}/tratamientos/veterinario/${veterinarioId}`,
  TRATAMIENTOS_BY_CLIENTE: (clienteId: number) => `${API_BASE_URL}/tratamientos/cliente/${clienteId}`,

  // TratamientoDroga
  TRATAMIENTO_DROGAS: `${API_BASE_URL}/tratamiento-drogas`,
  TRATAMIENTO_DROGAS_BY_TRATAMIENTO: (tratamientoId: number) => `${API_BASE_URL}/tratamiento-drogas/tratamiento/${tratamientoId}`,

  // Dashboard
  DASHBOARD_METRICAS: `${API_BASE_URL}/dashboard/metricas`,

  // Cita
  CITAS: `${API_BASE_URL}/citas`,
  CITAS_BY_VETERINARIO: (veterinarioId: number) => `${API_BASE_URL}/citas/veterinario/${veterinarioId}`,
  CITAS_BY_MASCOTA: (mascotaId: number) => `${API_BASE_URL}/citas/mascota/${mascotaId}`,
  CITAS_BY_CLIENTE: (clienteId: number) => `${API_BASE_URL}/citas/cliente/${clienteId}`,
  CITAS_CANCELAR: (id: number) => `${API_BASE_URL}/citas/${id}/cancelar`,
};