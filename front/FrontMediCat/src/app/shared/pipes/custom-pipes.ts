import { Pipe, PipeTransform } from '@angular/core';
import { formatCOP, formatFecha, capitalizar, truncar } from '../utils/helpers';

/**
 * Formatea un número como pesos colombianos
 * Uso: {{ 15000 | cop }}  →  "$ 15.000"
 */
@Pipe({ name: 'cop', standalone: true })
export class CopPipe implements PipeTransform {
  transform(value: number): string {
    return formatCOP(value);
  }
}

/**
 * Formatea fecha ISO a texto legible en español
 * Uso: {{ '2026-03-15' | fechaLegible }}  →  "15 de marzo de 2026"
 */
@Pipe({ name: 'fechaLegible', standalone: true })
export class FechaLegiblePipe implements PipeTransform {
  transform(value: string): string {
    return formatFecha(value);
  }
}

/**
 * Capitaliza la primera letra
 * Uso: {{ 'canino' | capitalizar }}  →  "Canino"
 */
@Pipe({ name: 'capitalizar', standalone: true })
export class CapitalizarPipe implements PipeTransform {
  transform(value: string): string {
    return capitalizar(value);
  }
}

/**
 * Trunca un texto largo
 * Uso: {{ texto | truncar:50 }}
 */
@Pipe({ name: 'truncar', standalone: true })
export class TruncarPipe implements PipeTransform {
  transform(value: string, max = 60): string {
    return truncar(value, max);
  }
}

/**
 * Convierte número de mes "03" → "Mar"
 * Uso: {{ t.fecha | slice:5:7 | mesNombre }}
 */
const MESES: Record<string, string> = {
  '01': 'Ene', '02': 'Feb', '03': 'Mar', '04': 'Abr',
  '05': 'May', '06': 'Jun', '07': 'Jul', '08': 'Ago',
  '09': 'Sep', '10': 'Oct', '11': 'Nov', '12': 'Dic',
};
 
@Pipe({ name: 'mesNombre', standalone: true })
export class MesNombrePipe implements PipeTransform {
  transform(value: string): string {
    return MESES[value] ?? value;
  }
}
@Pipe({ name: 'estadoBadge', standalone: true })
export class EstadoBadgePipe implements PipeTransform {
  private readonly map: Record<string, string> = {
    activa:      '✅ Activa',
    tratamiento: '💊 En tratamiento',
    inactiva:    '❌ Inactiva',
    activo:      '✅ Activo',
    inactivo:    '❌ Inactivo',
  };

  transform(value: string): string {
    return this.map[value?.toLowerCase()] ?? value;
  }

  
}