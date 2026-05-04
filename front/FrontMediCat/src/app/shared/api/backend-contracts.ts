// ============================================
// ENTIDAD: Admin
// ============================================
export interface Admin {
  id: number;
  nombre: string;
  correo: string;
  contrasenia: string;
}

export interface AdminRequest {
  nombre: string;
  correo: string;
  contrasenia: string;
}

// ============================================
// ENTIDAD: Cliente
// ============================================
export interface Cliente {
  id: number;
  nombre: string;
  apellido: string;
  correo: string;
  contrasenia: string;
  celular: string;
  mascotas?: Mascota[];
  citas?: Cita[];
}

export interface ClienteRequest {
  nombre: string;
  apellido: string;
  correo: string;
  contrasenia?: string;
  celular: string;
}

// ============================================
// ENTIDAD: Veterinario
// ============================================
export interface Veterinario {
  id: number;
  nombre: string;
  cedula: string;
  celular: string;
  correo: string;
  especialidad: string;
  contrasenia: string;
  imageUrl: string;
  estado: 'activo' | 'inactivo';
  numAtenciones: number;
}

export interface VeterinarioRequest {
  nombre: string;
  cedula: string;
  celular: string;
  correo: string;
  especialidad: string;
  contrasenia: string;
  imageUrl?: string;
  estado?: 'activo' | 'inactivo';
}

// ============================================
// ENTIDAD: Mascota
// ============================================
export interface Mascota {
  id: number;
  nombre: string;
  especie: string;
  raza: string;
  sexo: 'Macho' | 'Hembra';
  fechaNacimiento: string;  // ISO Date: "YYYY-MM-DD"
  edad: number;
  peso: number;
  enfermedad: string;
  observaciones: string;
  foto: string;
  estado: 'ACTIVA' | 'TRATAMIENTO' | 'INACTIVA';
  cliente: Cliente | null;
}

export interface MascotaRequest {
  nombre: string;
  especie: string;
  raza: string;
  sexo: 'Macho' | 'Hembra';
  fechaNacimiento: string;  // ISO Date: "YYYY-MM-DD"
  edad?: number;
  peso: number;
  enfermedad?: string;
  observaciones?: string;
  foto?: string;
  estado?: 'ACTIVA' | 'TRATAMIENTO' | 'INACTIVA';
}

// ============================================
// ENTIDAD: Droga
// ============================================
export interface Droga {
  id: number;
  nombre: string;
  precioCompra: number;
  precioVenta: number;
  unidadesDisponibles: number;
  unidadesVendidas: number;
}

export interface DrogaRequest {
  nombre: string;
  precioCompra: number;
  precioVenta: number;
  unidadesDisponibles: number;
}

// ============================================
// ENTIDAD: Tratamiento
// ============================================
export interface Tratamiento {
  id: number;
  diagnostico: string;
  observaciones: string;
  fecha: string;  // ISO Date: "YYYY-MM-DD"
  estado: 'PENDIENTE' | 'COMPLETADO' | 'CANCELADO';
  mascota: Mascota;
  veterinario: Veterinario;
  drogas: TratamientoDroga[];
}

export interface TratamientoRequest {
  diagnostico: string;
  observaciones: string;
  fecha: string;  // ISO Date: "YYYY-MM-DD"
  estado?: 'PENDIENTE' | 'COMPLETADO' | 'CANCELADO';
}

// ============================================
// ENTIDAD: TratamientoDroga
// ============================================
export interface TratamientoDroga {
  id: number;
  tratamiento: Tratamiento;
  droga: Droga;
  cantidad: number;
}

export interface TratamientoDrogaRequest {
  tratamientoId: number;
  drogaId: number;
  cantidad: number;
}

// ============================================
// ENTIDAD: Cita
// ============================================
export interface Cita {
  id: number;
  fechaInicio: string;  // ISO DateTime: "YYYY-MM-DDTHH:mm:ss"
  fechaFin: string;     // ISO DateTime: "YYYY-MM-DDTHH:mm:ss"
  motivo: string;
  estado: 'PENDIENTE' | 'CONFIRMADA' | 'REALIZADA' | 'CANCELADA';
  cliente: Cliente;
  mascota: Mascota;
  veterinario: Veterinario;
}

export interface CitaRequest {
  fechaInicio: string;  // ISO DateTime: "YYYY-MM-DDTHH:mm:ss"
  fechaFin: string;     // ISO DateTime: "YYYY-MM-DDTHH:mm:ss"
  motivo: string;
  estado?: 'PENDIENTE' | 'CONFIRMADA' | 'REALIZADA' | 'CANCELADA';
}

// ============================================
// LOGIN / AUTENTICACIÓN
// ============================================
export interface LoginRequest {
  correo: string;
  contrasenia: string;
}

export interface LoginResponse {
  id: number;
  nombre: string;
  correo: string;
  rol: 'ADMIN' | 'VETERINARIO' | 'CLIENTE';
  token?: string;  // Para cuando implementemos JWT
}

// ============================================
// RESPUESTAS DE ERROR (GlobalExceptionHandler)
// ============================================
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface ValidationErrorResponse extends ErrorResponse {
  validationErrors: Record<string, string>;
}

// ============================================
// DTOs DE AGREGACIÓN (compartidos por varios endpoints)
// ============================================
export interface MedicamentoCantidad {
  nombre: string;
  cantidad: number;
}

export interface CitaResumen {
  hora: string;
  mascota: string;
  duenio: string;
  tipo: string;
  estado: 'PENDIENTE' | 'CONFIRMADA' | 'REALIZADA' | 'CANCELADA';
}

// ============================================
// FILTROS DE BÚSQUEDA
// ============================================
export interface MascotaFiltros {
  query?: string;
  estado?: 'ACTIVA' | 'TRATAMIENTO' | 'INACTIVA';
}

export interface CitaFiltros {
  inicio?: string;
  fin?: string;
  veterinarioId?: number;
  mascotaId?: number;
  clienteId?: number;
}

export interface ClienteFiltros {
  query?: string;
}

// ============================================
// TIPOS PARA ACTUALIZACIÓN (PARCIAL)
// ============================================
export type ClienteUpdateDto = Partial<ClienteRequest>;
export type MascotaUpdateDto = Partial<MascotaRequest>;
export type VeterinarioUpdateDto = Partial<VeterinarioRequest>;
export type DrogaUpdateDto = Partial<DrogaRequest>;
export type TratamientoUpdateDto = Partial<TratamientoRequest>;
export type CitaUpdateDto = Partial<CitaRequest>;