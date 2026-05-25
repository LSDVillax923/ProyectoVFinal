package com.example.demo.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.demo.entities.Admin;
import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.TratamientoDroga;
import com.example.demo.entities.Veterinario;

public final class DtoMapper {

    private DtoMapper() {}

    // ── Cliente ──────────────────────────────────────────────────────────
    public static ClienteDto toClienteDto(Cliente c) {
        return toClienteDto(c, 0L);
    }

    public static ClienteDto toClienteDto(Cliente c, long mascotasCount) {
        if (c == null) return null;
        return new ClienteDto(
                c.getId(),
                c.getNombre(),
                c.getApellido(),
                c.getCedula(),
                c.getCorreo(),
                c.getCelular(),
                mascotasCount
        );
    }

    public static List<ClienteDto> toClienteDtoList(List<Cliente> list) {
        return list.stream().filter(Objects::nonNull).map(DtoMapper::toClienteDto).toList();
    }

    /** Variante con conteos de mascotas precalculados por ClienteController.findAll. */
    public static List<ClienteDto> toClienteDtoList(List<Cliente> list, Map<Long, Long> mascotasPorCliente) {
        return list.stream()
                .filter(Objects::nonNull)
                .map(c -> toClienteDto(c, mascotasPorCliente.getOrDefault(c.getId(), 0L)))
                .toList();
    }

    // ── Veterinario ──────────────────────────────────────────────────────
    public static VeterinarioDto toVeterinarioDto(Veterinario v) {
        if (v == null) return null;
        return new VeterinarioDto(
                v.getId(),
                v.getNombre(),
                v.getCedula(),
                v.getCelular(),
                v.getCorreo(),
                v.getEspecialidad(),
                v.getImageUrl(),
                v.getEstado(),
                v.getNumAtenciones()
        );
    }

    public static List<VeterinarioDto> toVeterinarioDtoList(List<Veterinario> list) {
        return list.stream().filter(Objects::nonNull).map(DtoMapper::toVeterinarioDto).toList();
    }

    // ── Admin ────────────────────────────────────────────────────────────
    public static AdminDto toAdminDto(Admin a) {
        if (a == null) return null;
        return new AdminDto(a.getId(), a.getNombre(), a.getCorreo());
    }

    public static List<AdminDto> toAdminDtoList(List<Admin> list) {
        return list.stream().filter(Objects::nonNull).map(DtoMapper::toAdminDto).toList();
    }

    // ── Mascota ──────────────────────────────────────────────────────────
    public static MascotaDto toMascotaDto(Mascota m) {
        if (m == null) return null;
        Cliente cli = m.getCliente();
        String clienteNombre = (cli != null)
                ? (safe(cli.getNombre()) + " " + safe(cli.getApellido())).trim()
                : null;
        return new MascotaDto(
                m.getId(),
                m.getNombre(),
                m.getEspecie(),
                m.getRaza(),
                m.getSexo(),
                m.getFechaNacimiento(),
                m.getEdad(),
                m.getPeso(),
                m.getFoto(),
                m.getEstado() != null ? m.getEstado().name() : null,
                m.getEnfermedad(),
                m.getObservaciones(),
                m.getTratamiento(),
                cli != null ? cli.getId() : null,
                clienteNombre
        );
    }

    public static List<MascotaDto> toMascotaDtoList(List<Mascota> list) {
        return list.stream().filter(Objects::nonNull).map(DtoMapper::toMascotaDto).toList();
    }

    // ── Cita ─────────────────────────────────────────────────────────────
    public static CitaDetalleDto toCitaDetalleDto(Cita c) {
        if (c == null) return null;
        Cliente cli = c.getCliente();
        Mascota m = c.getMascota();
        Veterinario v = c.getVeterinario();
        return new CitaDetalleDto(
                c.getId(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getMotivo(),
                c.getEstado() != null ? c.getEstado().name() : null,
                cli != null ? cli.getId() : null,
                cli != null ? (safe(cli.getNombre()) + " " + safe(cli.getApellido())).trim() : null,
                m != null ? m.getId() : null,
                m != null ? m.getNombre() : null,
                v != null ? v.getId() : null,
                v != null ? v.getNombre() : null
        );
    }

    public static List<CitaDetalleDto> toCitaDetalleDtoList(List<Cita> list) {
        return list.stream().filter(Objects::nonNull).map(DtoMapper::toCitaDetalleDto).toList();
    }

    // ── Tratamiento ──────────────────────────────────────────────────────
    public static TratamientoDto toTratamientoDto(Tratamiento t) {
        if (t == null) return null;
        Mascota m = t.getMascota();
        Veterinario v = t.getVeterinario();
        Long clienteId = (m != null && m.getCliente() != null) ? m.getCliente().getId() : null;
        List<TratamientoDrogaResumenDto> drogas = (t.getDrogas() == null) ? List.of()
                : t.getDrogas().stream().filter(Objects::nonNull).map(DtoMapper::toTratamientoDrogaResumenDto).toList();
        return new TratamientoDto(
                t.getId(),
                t.getDiagnostico(),
                t.getObservaciones(),
                t.getFecha(),
                t.getEstado() != null ? t.getEstado().name() : null,
                m != null ? m.getId() : null,
                m != null ? m.getNombre() : null,
                clienteId,
                v != null ? v.getId() : null,
                v != null ? v.getNombre() : null,
                drogas
        );
    }

    public static List<TratamientoDto> toTratamientoDtoList(List<Tratamiento> list) {
        return list.stream().filter(Objects::nonNull).map(DtoMapper::toTratamientoDto).toList();
    }

    // ── TratamientoDroga (resumen) ───────────────────────────────────────
    public static TratamientoDrogaResumenDto toTratamientoDrogaResumenDto(TratamientoDroga td) {
        if (td == null) return null;
        return new TratamientoDrogaResumenDto(
                td.getId(),
                td.getDroga() != null ? td.getDroga().getId() : null,
                td.getDroga() != null ? td.getDroga().getNombre() : null,
                td.getCantidad()
        );
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
