package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String contrasenia;
}
