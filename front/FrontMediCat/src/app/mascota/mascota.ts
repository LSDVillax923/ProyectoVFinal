export interface Mascota {
  id: number;
  nombre: string;
  especie: string;
  raza: string;
  sexo: string;
  fechaNacimiento: string;
  edad?: number;
  peso: number;
  enfermedad: string;
  observaciones: string;
  foto?: string;
  estado: 'ACTIVA' | 'TRATAMIENTO' | 'INACTIVA';
  clienteId: number;
  propietario?: string;
}
