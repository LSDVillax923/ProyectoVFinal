package com.example.demo.dto;

import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;

public final class DtoMapper {

    private DtoMapper() {
    }

    public static ClienteDto toDto(Cliente cliente) {
        if (cliente == null) return null;
        return new ClienteDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getCedula(),
                cliente.getCorreo(),
                cliente.getContrasenia(),
                cliente.getCelular()
        );
    }

    public static VeterinarioDto toDto(Veterinario veterinario) {
        if (veterinario == null) return null;
        return new VeterinarioDto(
                veterinario.getId(),
                veterinario.getNombre(),
                veterinario.getCedula(),
                veterinario.getCelular(),
                veterinario.getCorreo(),
                veterinario.getEspecialidad(),
                veterinario.getContrasenia(),
                veterinario.getImageUrl(),
                veterinario.getEstado(),
                veterinario.getNumAtenciones()
        );
    }

    public static MascotaDto toDto(Mascota mascota) {
        if (mascota == null) return null;
        return new MascotaDto(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getSexo(),
                mascota.getFechaNacimiento(),
                mascota.getEdad(),
                mascota.getPeso(),
                mascota.getFoto(),
                mascota.getEstado() != null ? mascota.getEstado().name() : null,
                mascota.getEnfermedad(),
                mascota.getObservaciones(),
                mascota.getTratamiento(),
                mascota.getCliente() != null ? mascota.getCliente().getId() : null
        );
    }

    public static TratamientoDto toDto(Tratamiento tratamiento) {
        if (tratamiento == null) return null;
        return new TratamientoDto(
                tratamiento.getId(),
                tratamiento.getDiagnostico(),
                tratamiento.getObservaciones(),
                tratamiento.getFecha(),
                tratamiento.getEstado() != null ? tratamiento.getEstado().name() : null,
                tratamiento.getMascota() != null ? tratamiento.getMascota().getId() : null,
                tratamiento.getVeterinario() != null ? tratamiento.getVeterinario().getId() : null
        );
    }

    public static CitaDetalleDto toDto(Cita cita) {
        if (cita == null) return null;

        Cliente cliente = cita.getCliente();
        Mascota mascota = cita.getMascota();
        Veterinario veterinario = cita.getVeterinario();

        String clienteNombre = cliente != null ? (safe(cliente.getNombre()) + " " + safe(cliente.getApellido())).trim() : null;

        return new CitaDetalleDto(
                cita.getId(),
                cita.getFechaInicio(),
                cita.getFechaFin(),
                cita.getMotivo(),
                cita.getEstado() != null ? cita.getEstado().name() : null,
                cliente != null ? cliente.getId() : null,
                clienteNombre,
                mascota != null ? mascota.getId() : null,
                mascota != null ? mascota.getNombre() : null,
                veterinario != null ? veterinario.getId() : null,
                veterinario != null ? veterinario.getNombre() : null
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}