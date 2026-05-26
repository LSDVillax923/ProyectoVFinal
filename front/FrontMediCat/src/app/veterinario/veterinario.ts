export interface Veterinario {
  id: number;
  nombre: string;
  cedula: string;
  celular: string;
  correo: string;
  especialidad: string;
  contrasenia?: string;
  estado: 'activo' | 'inactivo';
  numAtenciones?: number;
}
