import { MASCOTAS_FOTO_BASE } from '../api/rest-endpoints';

export function formatCOP(valor: number): string {
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  }).format(valor);
}

/**
 * Formatea una fecha ISO (yyyy-mm-dd) a texto legible en español
 */
export function formatFecha(fecha: string): string {
  if (!fecha) return '—';
  const [year, month, day] = fecha.split('-');
  const date = new Date(Number(year), Number(month) - 1, Number(day));
  return date.toLocaleDateString('es-CO', { year: 'numeric', month: 'long', day: 'numeric' });
}

/**
 * Retorna la fecha de hoy en formato yyyy-mm-dd (útil como valor por defecto en inputs date)
 */
export function fechaHoy(): string {
  return new Date().toISOString().split('T')[0];
}

/**
 * Genera un ID único basado en timestamp
 */
export function generarId(): number {
  return Date.now();
}

/**
 * Capitaliza la primera letra de un string
 */
export function capitalizar(texto: string): string {
  if (!texto) return '';
  return texto.charAt(0).toUpperCase() + texto.slice(1).toLowerCase();
}

/**
 * Trunca un texto a N caracteres con elipsis
 */
export function truncar(texto: string, max = 60): string {
  if (!texto || texto.length <= max) return texto;
  return texto.slice(0, max).trimEnd() + '…';
}

/**
 * Verifica si un correo tiene formato válido
 */
export function esCorreoValido(correo: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo.trim());
}

/**
 * Devuelve las iniciales de un nombre completo
 */
export function iniciales(nombreCompleto: string): string {
  return nombreCompleto
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0].toUpperCase())
    .join('');
}

/**
 * Resuelve la URL de la foto de una mascota.
 * Si el nombre del archivo empieza con "pet" se sirve desde el backend (uploads),
 * de lo contrario se asume que es una imagen de assets.
 */
export function urlFotoMascota(foto?: string | null): string {
  if (!foto) return 'assets/img/default-pet.png';
  if (foto.startsWith('pet')) return `${MASCOTAS_FOTO_BASE}/${foto}`;
  return `assets/img/${foto}`;
}