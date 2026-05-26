import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { Mascota } from '../mascota';
import { Tratamiento } from '../../tratamiento/tratamiento';

/**
 * Genera el historial médico de una mascota en PDF.
 * La portada lleva los datos del animal (sin la foto) y luego la tabla
 * de tratamientos ordenados por fecha descendente.
 */
@Injectable({ providedIn: 'root' })
export class HistorialPdfService {

  generar(mascota: Mascota, tratamientos: Tratamiento[]): void {
    const doc = new jsPDF({ unit: 'pt', format: 'a4' });
    const margenX = 40;

    this.pintarEncabezado(doc, mascota, margenX);
    const yDespuesDatos = this.pintarDatosMascota(doc, mascota, margenX);
    this.pintarTabla(doc, tratamientos, yDespuesDatos);
    this.pintarPie(doc);

    const nombreArchivo = `historial-${this.slug(mascota.nombre)}-${this.fechaArchivo()}.pdf`;
    doc.save(nombreArchivo);
  }

  // ── Secciones ────────────────────────────────────────────────────────────
  private pintarEncabezado(doc: jsPDF, mascota: Mascota, margenX: number): void {
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(20);
    doc.setTextColor(7, 104, 109);
    doc.text('Historial médico', margenX, 60);

    doc.setFontSize(13);
    doc.setTextColor(60, 60, 60);
    doc.text(`Mascota: ${mascota.nombre}`, margenX, 82);

    doc.setDrawColor(7, 104, 109);
    doc.setLineWidth(1);
    doc.line(margenX, 94, doc.internal.pageSize.getWidth() - margenX, 94);
  }

  private pintarDatosMascota(doc: jsPDF, mascota: Mascota, margenX: number): number {
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.setTextColor(7, 104, 109);
    doc.text('Datos de la mascota', margenX, 120);

    const filas: Array<[string, string]> = [
      ['Nombre',        mascota.nombre],
      ['Especie',       mascota.especie],
      ['Raza',          mascota.raza],
      ['Sexo',          mascota.sexo],
      ['Fecha de nac.', mascota.fechaNacimiento],
      ['Edad',          `${mascota.edad ?? '—'} año(s)`],
      ['Peso',          `${mascota.peso} kg`],
      ['Estado',        this.textoEstado(mascota.estado)],
      ['Enfermedad',    mascota.enfermedad || 'Ninguna'],
      ['Propietario',   mascota.propietario || '—'],
      ['Observaciones', mascota.observaciones || '—'],
    ];

    autoTable(doc, {
      startY: 130,
      head: [],
      body: filas,
      theme: 'grid',
      styles: { fontSize: 10, cellPadding: 6, textColor: [30, 30, 30] },
      columnStyles: {
        0: { fontStyle: 'bold', fillColor: [242, 237, 231], textColor: [7, 104, 109], cellWidth: 130 },
        1: { fillColor: [255, 255, 255] },
      },
      margin: { left: margenX, right: margenX },
    });

    // jspdf-autotable expone la Y final como lastAutoTable.finalY
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    return ((doc as any).lastAutoTable?.finalY ?? 130) + 30;
  }

  private pintarTabla(doc: jsPDF, tratamientos: Tratamiento[], yInicio: number): void {
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.setTextColor(7, 104, 109);
    doc.text('Tratamientos', 40, yInicio);

    if (!tratamientos.length) {
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(11);
      doc.setTextColor(80, 80, 80);
      doc.text('Sin tratamientos registrados para esta mascota.', 40, yInicio + 20);
      return;
    }

    const filas = [...tratamientos]
      .sort((a, b) => (b.fecha ?? '').localeCompare(a.fecha ?? ''))
      .map((t) => [
        t.fecha,
        t.diagnostico,
        t.veterinario || '—',
        this.textoEstadoTratamiento(t.estado),
        (t.observaciones || '—').slice(0, 80),
      ]);

    autoTable(doc, {
      startY: yInicio + 10,
      head: [['Fecha', 'Diagnóstico', 'Veterinario', 'Estado', 'Observaciones']],
      body: filas,
      theme: 'striped',
      headStyles: { fillColor: [7, 104, 109], textColor: 255, fontStyle: 'bold' },
      styles: { fontSize: 9, cellPadding: 5, textColor: [30, 30, 30] },
      alternateRowStyles: { fillColor: [247, 244, 240] },
      margin: { left: 40, right: 40 },
    });
  }

  private pintarPie(doc: jsPDF): void {
    const totalPaginas = doc.getNumberOfPages();
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.setTextColor(120, 120, 120);

    const ancho = doc.internal.pageSize.getWidth();
    const alto = doc.internal.pageSize.getHeight();
    const generadoEn = new Date().toLocaleString('es-CO');

    for (let i = 1; i <= totalPaginas; i++) {
      doc.setPage(i);
      doc.text(`MediCat · Generado ${generadoEn}`, 40, alto - 20);
      doc.text(`Página ${i} de ${totalPaginas}`, ancho - 40, alto - 20, { align: 'right' });
    }
  }

  // ── Helpers ──────────────────────────────────────────────────────────────
  private textoEstado(estado: Mascota['estado']): string {
    const map: Record<Mascota['estado'], string> = {
      ACTIVA: 'Activa',
      TRATAMIENTO: 'En tratamiento',
      INACTIVA: 'Inactiva',
    };
    return map[estado] ?? estado;
  }

  private textoEstadoTratamiento(estado: string): string {
    const map: Record<string, string> = {
      PENDIENTE: 'Pendiente',
      COMPLETADO: 'Completado',
      CANCELADO: 'Cancelado',
    };
    return map[estado] ?? estado;
  }

  private slug(texto: string): string {
    return (texto || 'mascota')
      .toLowerCase()
      .normalize('NFD').replace(/[̀-ͯ]/g, '')
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/^-|-$/g, '');
  }

  private fechaArchivo(): string {
    const d = new Date();
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}`;
  }
}
