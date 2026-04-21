import { Injectable } from '@angular/core';
import { Tratamiento } from '../tratamiento';
import { TRATAMIENTOS_MOCK } from '../../shared/data/mock-data';

@Injectable({ providedIn: 'root' })
export class TratamientoService {
  private tratamientos: Tratamiento[] = [];
  private nextId = 1;

  constructor() {
    this.tratamientos = [...TRATAMIENTOS_MOCK];
    this.nextId = Math.max(...this.tratamientos.map(t => t.id), 0) + 1;
  }

  getAll(): Tratamiento[] {
    return this.tratamientos;
  }

  getById(id: number): Tratamiento | null {
    return this.tratamientos.find((t) => t.id === id) ?? null;
  }

  getByMascotaId(mascotaId: number): Tratamiento[] {
    return this.tratamientos.filter((t) => t.mascotaId === mascotaId);
  }

  getByClienteId(clienteId: number): Tratamiento[] {
    return this.tratamientos.filter((t) => t.clienteId === clienteId);
  }

  add(tratamiento: Omit<Tratamiento, 'id'>): Tratamiento {
    const nuevo: Tratamiento = { ...tratamiento, id: this.nextId++ };
    this.tratamientos.push(nuevo);
    return nuevo;
  }

  update(id: number, cambios: Partial<Tratamiento>): Tratamiento | null {
    const idx = this.tratamientos.findIndex((t) => t.id === id);
    if (idx === -1) return null;
    this.tratamientos[idx] = { ...this.tratamientos[idx], ...cambios };
    return this.tratamientos[idx];
  }

  /**
   * Elimina un tratamiento específico por ID
   */
  delete(id: number): boolean {
    const antes = this.tratamientos.length;
    this.tratamientos = this.tratamientos.filter((t) => t.id !== id);
    return this.tratamientos.length < antes;
  }

  /**
   * ELIMINACIÓN EN CASCADA
   * Elimina todos los tratamientos de una mascota específica
   * Retorna el número de tratamientos eliminados
   */
  deleteByMascotaId(mascotaId: number): number {
    const antes = this.tratamientos.length;
    this.tratamientos = this.tratamientos.filter((t) => t.mascotaId !== mascotaId);
    const eliminados = antes - this.tratamientos.length;
    console.log(`  Eliminados ${eliminados} tratamiento(s) de la mascota ID ${mascotaId}`);
    return eliminados;
  }

  /**
   * Elimina todos los tratamientos de un cliente específico
   * Retorna el número de tratamientos eliminados
   */
  deleteByClienteId(clienteId: number): number {
    const antes = this.tratamientos.length;
    this.tratamientos = this.tratamientos.filter((t) => t.clienteId !== clienteId);
    const eliminados = antes - this.tratamientos.length;
    console.log(`  Eliminados ${eliminados} tratamiento(s) del cliente ID ${clienteId}`);
    return eliminados;
  }

  search(query: string): Tratamiento[] {
    const filtro = query.trim().toLowerCase();
    if (!filtro) return this.tratamientos;
    return this.tratamientos.filter(
      (t) =>
        t.mascota.toLowerCase().includes(filtro) ||
        t.veterinario.toLowerCase().includes(filtro) ||
        t.diagnostico.toLowerCase().includes(filtro),
    );
  }
}