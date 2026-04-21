import { TratamientoDroga } from '../tratamiento-droga/tratamiento-droga';

export interface Tratamiento {
  id: number;
  mascotaId: number;
  mascota: string;
  clienteId: number;
  veterinarioId: number;
  veterinario: string;
  diagnostico: string;
  observaciones: string;
  fecha: string;
  estado: 'PENDIENTE' | 'COMPLETADO' | 'CANCELADO';
  drogas: TratamientoDroga[];
}
