import { Cliente, Mascota as BackendMascota, MascotaRequest, Veterinario as BackendVeterinario, Tratamiento as BackendTratamiento, Droga as BackendDroga } from './backend-contracts';
import { TratamientoRequest } from './backend-contracts';

// ============================================
// CONVERSIÓN DE MODELOS
// ============================================

/**
 * Convierte un objeto Mascota del backend a un formato para formulario
 */
export function mascotaToRequest(mascota: BackendMascota): MascotaRequest {
  return {
    nombre: mascota.nombre,
    especie: mascota.especie,
    raza: mascota.raza,
    sexo: mascota.sexo,
    fechaNacimiento: mascota.fechaNacimiento,
    peso: mascota.peso,
    enfermedad: mascota.enfermedad,
    observaciones: mascota.observaciones,
    foto: mascota.foto,
    estado: mascota.estado,
  };
}

// ============================================
// FORMATEO DE FECHAS
// ============================================

/**
 * Formatea una fecha ISO para mostrar en la UI
 */
export function formatearFecha(fecha: string | Date, formato: 'corta' | 'larga' | 'hora' | 'completa' = 'corta'): string {
  const date = typeof fecha === 'string' ? new Date(fecha) : fecha;
  
  // Validar fecha
  if (isNaN(date.getTime())) {
    return 'Fecha inválida';
  }
  
  const opciones: Intl.DateTimeFormatOptions = {
    timeZone: 'America/Bogota'
  };
  
  switch (formato) {
    case 'corta':
      return date.toLocaleDateString('es-CO', {
        ...opciones,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
      
    case 'larga':
      return date.toLocaleDateString('es-CO', {
        ...opciones,
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
      
    case 'hora':
      return date.toLocaleTimeString('es-CO', {
        ...opciones,
        hour: '2-digit',
        minute: '2-digit'
      });
      
    case 'completa':
      return date.toLocaleString('es-CO', {
        ...opciones,
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
      
    default:
      return date.toLocaleDateString('es-CO');
  }
}

/**
 * Formatea solo la hora de una fecha ISO
 */
export function formatearHora(fecha: string | Date): string {
  return formatearFecha(fecha, 'hora');
}

/**
 * Formatea un rango de horas (ej: "09:00 - 10:30")
 */
export function formatearRangoHorario(fechaInicio: string, fechaFin: string): string {
  return `${formatearHora(fechaInicio)} - ${formatearHora(fechaFin)}`;
}

// ============================================
// CÁLCULO DE EDAD (FRONTEND - Solo para mostrar)
// ============================================

/**
 * Calcula la edad en años a partir de una fecha de nacimiento
 * NOTA: El backend es la fuente de verdad. Esto es solo para UX.
 */
export function calcularEdadFrontend(fechaNacimiento: string | Date): number {
  const hoy = new Date();
  const nacimiento = typeof fechaNacimiento === 'string' 
    ? new Date(fechaNacimiento) 
    : fechaNacimiento;
  
  let edad = hoy.getFullYear() - nacimiento.getFullYear();
  const mes = hoy.getMonth() - nacimiento.getMonth();
  
  if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) {
    edad--;
  }
  
  return edad;
}

/**
 * Calcula la edad en meses (para cachorros)
 */
export function calcularEdadEnMesesFrontend(fechaNacimiento: string | Date): number {
  const hoy = new Date();
  const nacimiento = typeof fechaNacimiento === 'string' 
    ? new Date(fechaNacimiento) 
    : fechaNacimiento;
  
  let meses = (hoy.getFullYear() - nacimiento.getFullYear()) * 12;
  meses += hoy.getMonth() - nacimiento.getMonth();
  
  if (hoy.getDate() < nacimiento.getDate()) {
    meses--;
  }
  
  return meses;
}

/**
 * Formatea la edad para mostrar (ej: "3 años" o "6 meses")
 */
export function formatearEdad(fechaNacimiento: string | Date): string {
  const meses = calcularEdadEnMesesFrontend(fechaNacimiento);
  
  if (meses < 12) {
    return `${meses} ${meses === 1 ? 'mes' : 'meses'}`;
  }
  
  const años = Math.floor(meses / 12);
  const mesesRestantes = meses % 12;
  
  if (mesesRestantes === 0) {
    return `${años} ${años === 1 ? 'año' : 'años'}`;
  }
  
  return `${años} ${años === 1 ? 'año' : 'años'} y ${mesesRestantes} ${mesesRestantes === 1 ? 'mes' : 'meses'}`;
}

// ============================================
// CONVERSIÓN A ISO
// ============================================

/**
 * Convierte una fecha local a ISO Date para enviar al backend (YYYY-MM-DD)
 */
export function toISODate(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

/**
 * Convierte una fecha y hora local a ISO DateTime para enviar al backend
 */
export function toISODateTime(date: Date): string {
  return date.toISOString();
}

/**
 * Obtiene la fecha de hoy en formato ISO (YYYY-MM-DD)
 */
export function getHoyISO(): string {
  return toISODate(new Date());
}

/**
 * Obtiene la fecha de mañana en formato ISO
 */
export function getManianaISO(): string {
  const maniana = new Date();
  maniana.setDate(maniana.getDate() + 1);
  return toISODate(maniana);
}

// ============================================
// NOMBRES Y TEXTOS
// ============================================

/**
 * Obtiene el nombre completo de un cliente
 */
export function nombreCompletoCliente(cliente: Cliente | null | undefined): string { // eslint-disable-line @typescript-eslint/no-unused-vars
  if (!cliente) return 'Sin cliente';
  return `${cliente.nombre} ${cliente.apellido || ''}`.trim();
}

/**
 * Obtiene el nombre completo de un veterinario
 */
export function nombreCompletoVeterinario(veterinario: BackendVeterinario | null | undefined): string {
  if (!veterinario) return 'No asignado';
  return veterinario.nombre;
}

/**
 * Obtiene las iniciales de un nombre (para avatares)
 */
export function getIniciales(nombre: string): string {
  if (!nombre) return '?';
  
  return nombre
    .split(' ')
    .filter(p => p.length > 0)
    .slice(0, 2)
    .map(p => p[0].toUpperCase())
    .join('');
}

/**
 * Capitaliza la primera letra de cada palabra
 */
export function capitalizarPalabras(texto: string): string {
  if (!texto) return '';
  
  return texto
    .toLowerCase()
    .split(' ')
    .map(palabra => palabra.charAt(0).toUpperCase() + palabra.slice(1))
    .join(' ');
}

// ============================================
// CLASES CSS POR ESTADO
// ============================================

/**
 * Obtiene la clase CSS según el estado de la mascota
 */
export function getClaseEstadoMascota(estado: string): string {
  const clases: Record<string, string> = {
    'ACTIVA': 'badge-success',
    'TRATAMIENTO': 'badge-warning',
    'INACTIVA': 'badge-secondary'
  };
  return clases[estado] || 'badge-light';
}

/**
 * Obtiene el texto traducido del estado de la mascota
 */
export function getTextoEstadoMascota(estado: string): string {
  const textos: Record<string, string> = {
    'ACTIVA': 'Activa',
    'TRATAMIENTO': 'En tratamiento',
    'INACTIVA': 'Inactiva'
  };
  return textos[estado] || estado;
}

/**
 * Obtiene la clase CSS según el estado de la cita
 */
export function getClaseEstadoCita(estado: string): string {
  const clases: Record<string, string> = {
    'PENDIENTE': 'badge-warning',
    'CONFIRMADA': 'badge-info',
    'REALIZADA': 'badge-success',
    'CANCELADA': 'badge-danger'
  };
  return clases[estado] || 'badge-light';
}

/**
 * Obtiene el texto traducido del estado de la cita
 */
export function getTextoEstadoCita(estado: string): string {
  const textos: Record<string, string> = {
    'PENDIENTE': 'Pendiente',
    'CONFIRMADA': 'Confirmada',
    'REALIZADA': 'Realizada',
    'CANCELADA': 'Cancelada'
  };
  return textos[estado] || estado;
}

/**
 * Obtiene la clase CSS según el estado del tratamiento
 */
export function getClaseEstadoTratamiento(estado: string): string {
  const clases: Record<string, string> = {
    'PENDIENTE': 'badge-warning',
    'COMPLETADO': 'badge-success',
    'CANCELADO': 'badge-danger'
  };
  return clases[estado] || 'badge-light';
}

/**
 * Obtiene el texto traducido del estado del tratamiento
 */
export function getTextoEstadoTratamiento(estado: string): string {
  const textos: Record<string, string> = {
    'PENDIENTE': 'Pendiente',
    'COMPLETADO': 'Completado',
    'CANCELADO': 'Cancelado'
  };
  return textos[estado] || estado;
}

/**
 * Obtiene la clase CSS según el estado del veterinario
 */
export function getClaseEstadoVeterinario(estado: string): string {
  const clases: Record<string, string> = {
    'activo': 'badge-success',
    'inactivo': 'badge-secondary'
  };
  return clases[estado] || 'badge-light';
}

/**
 * Obtiene el texto traducido del estado del veterinario
 */
export function getTextoEstadoVeterinario(estado: string): string {
  const textos: Record<string, string> = {
    'activo': 'Activo',
    'inactivo': 'Inactivo'
  };
  return textos[estado] || estado;
}

// ============================================
// VALIDACIONES
// ============================================

/**
 * Valida si un correo electrónico tiene formato válido
 */
export function esCorreoValido(correo: string): boolean {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(correo);
}

/**
 * Valida si un número de celular colombiano tiene formato válido
 */
export function esCelularValido(celular: string): boolean {
  const regex = /^3\d{9}$/;
  return regex.test(celular.replace(/\D/g, ''));
}

/**
 * Valida si una contraseña es segura (mínimo 6 caracteres)
 */
export function esContraseniaSegura(contrasenia: string): boolean {
  return contrasenia.length >= 6;
}

// ============================================
// UTILIDADES DE TEXTO
// ============================================

/**
 * Trunca un texto a una longitud máxima
 */
export function truncarTexto(texto: string, maxLength: number = 50): string {
  if (!texto || texto.length <= maxLength) return texto;
  return texto.substring(0, maxLength) + '...';
}

// ============================================
// COLORES Y VISUALIZACIÓN
// ============================================

/**
 * Obtiene un color basado en un string (para avatares consistentes)
 */
export function getColorFromString(str: string): string {
  const colors = [
    '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7',
    '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9'
  ];
  
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  
  return colors[Math.abs(hash) % colors.length];
}

/**
 * Obtiene el icono según la especie de mascota
 */
export function getIconoEspecie(especie: string): string {
  const iconos: Record<string, string> = {
    'Perro': '🐕',
    'Gato': '🐈',
  };
  return iconos[especie] || '🐾';
}

// ============================================
// FORMATO DE MONEDA
// ============================================

/**
 * Formatea un número como moneda colombiana (COP)
 */
export function formatearMoneda(valor: number): string {
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(valor);
}

// ============================================
// DURACIÓN Y TIEMPO
// ============================================

/**
 * Calcula la duración en minutos entre dos fechas ISO
 */
export function calcularDuracionMinutos(fechaInicio: string, fechaFin: string): number {
  const inicio = new Date(fechaInicio);
  const fin = new Date(fechaFin);
  return Math.round((fin.getTime() - inicio.getTime()) / (1000 * 60));
}

/**
 * Formatea una duración en minutos a formato legible
 */
export function formatearDuracion(minutos: number): string {
  if (minutos < 60) {
    return `${minutos} min`;
  }
  const horas = Math.floor(minutos / 60);
  const minsRestantes = minutos % 60;
  
  if (minsRestantes === 0) {
    return `${horas} ${horas === 1 ? 'hora' : 'horas'}`;
  }
  
  return `${horas}h ${minsRestantes}min`;
}

/**
 * Calcula el tiempo relativo (ej: "hace 5 minutos", "en 2 horas")
 */
export function tiempoRelativo(fecha: string | Date): string {
  const date = typeof fecha === 'string' ? new Date(fecha) : fecha;
  const ahora = new Date();
  const diffMs = date.getTime() - ahora.getTime();
  const diffMin = Math.round(diffMs / (1000 * 60));
  const diffHoras = Math.round(diffMs / (1000 * 60 * 60));
  const diffDias = Math.round(diffMs / (1000 * 60 * 60 * 24));
  
  const rtf = new Intl.RelativeTimeFormat('es', { numeric: 'auto' });
  
  if (Math.abs(diffDias) >= 1) {
    return rtf.format(diffDias, 'day');
  } else if (Math.abs(diffHoras) >= 1) {
    return rtf.format(diffHoras, 'hour');
  } else {
    return rtf.format(diffMin, 'minute');
  }
}

// ============================================
// EXPORTACIÓN POR DEFECTO (opcional)
// ============================================

export default {
  // Conversión
  mascotaToRequest,
  
  // Fechas
  formatearFecha,
  formatearHora,
  formatearRangoHorario,
  toISODate,
  toISODateTime,
  getHoyISO,
  getManianaISO,
  
  // Edad
  calcularEdadFrontend,
  calcularEdadEnMesesFrontend,
  formatearEdad,
  
  // Nombres
  nombreCompletoCliente,
  nombreCompletoVeterinario,
  getIniciales,
  capitalizarPalabras,
  
  // Estados
  getClaseEstadoMascota,
  getTextoEstadoMascota,
  getClaseEstadoCita,
  getTextoEstadoCita,
  getClaseEstadoTratamiento,
  getTextoEstadoTratamiento,
  getClaseEstadoVeterinario,
  getTextoEstadoVeterinario,
  
  // Validaciones
  esCorreoValido,
  esCelularValido,
  esContraseniaSegura,
  
  // Utilidades
  truncarTexto,
  getColorFromString,
  getIconoEspecie,
  formatearMoneda,
  calcularDuracionMinutos,
  formatearDuracion,
  tiempoRelativo
};

// ============================================
// MAPPERS LEGACY (OLD ↔ NEW)
// ============================================

export const ClienteMapper = {
  fromDto(dto: Cliente): Cliente {
    return { ...dto };
  },
};

export const MascotaMapper = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  fromDto(dto: BackendMascota): any {
    return {
      id: dto.id,
      nombre: dto.nombre,
      especie: dto.especie,
      raza: dto.raza,
      sexo: dto.sexo,
      fechaNacimiento: dto.fechaNacimiento,
      edad: dto.edad,
      peso: dto.peso,
      enfermedad: dto.enfermedad ?? '',
      observaciones: dto.observaciones ?? '',
      foto: dto.foto,
      estado: dto.estado ?? 'ACTIVA',
      clienteId: dto.cliente?.id ?? 0,
      propietario: dto.cliente
        ? `${dto.cliente.nombre} ${dto.cliente.apellido}`.trim()
        : '',
    };
  },
};

export const VeterinarioMapper = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  fromDto(dto: BackendVeterinario): any {
    return {
      id: dto.id,
      nombre: dto.nombre,
      cedula: dto.cedula,
      celular: dto.celular,
      correo: dto.correo,
      especialidad: dto.especialidad,
      contrasenia: dto.contrasenia,
      imageUrl: dto.imageUrl,
      estado: dto.estado,
      numAtenciones: dto.numAtenciones,
    };
  },
};

export const DrogaMapper = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  fromDto(dto: BackendDroga): any {
    return {
      id: dto.id,
      nombre: dto.nombre,
      precioCompra: dto.precioCompra,
      precioVenta: dto.precioVenta,
      unidadesDisponibles: dto.unidadesDisponibles,
      unidadesVendidas: dto.unidadesVendidas,
    };
  },
};

export const TratamientoMapper = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  fromDto(dto: BackendTratamiento): any {
    return {
      id: dto.id,
      mascotaId: dto.mascota?.id ?? 0,
      mascota: dto.mascota?.nombre ?? '',
      clienteId: dto.mascota?.cliente?.id ?? 0,
      veterinarioId: dto.veterinario?.id ?? 0,
      veterinario: dto.veterinario?.nombre ?? '',
      diagnostico: dto.diagnostico,
      observaciones: dto.observaciones,
      fecha: dto.fecha,
      estado: dto.estado ?? 'PENDIENTE',
      drogas: (dto.drogas ?? []).map((td) => ({
        id: td.id,
        drogaId: td.droga?.id ?? 0,
        nombreDroga: td.droga?.nombre ?? '',
        cantidad: td.cantidad,
      })),
    };
  },
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  toDto(tratamiento: any): TratamientoRequest {
    return {
      diagnostico: tratamiento.diagnostico ?? '',
      observaciones: tratamiento.observaciones ?? '',
      fecha: tratamiento.fecha ?? '',
      estado: tratamiento.estado ?? 'PENDIENTE',
    };
  },
};