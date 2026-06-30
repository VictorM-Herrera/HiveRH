package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SuspensionRequestDTO(

        @NotBlank(message = "El DNI del empleado es obligatorio")
        @Pattern(
                regexp = "^\\d{7,8}$",
                message = "El DNI debe contener 7 u 8 números, sin puntos, letras ni espacios"
        )
        String dniEmployee,

        @NotBlank(message = "El motivo de la suspensión es obligatorio")
        @Size(max = 255, message = "El motivo no puede superar los 255 caracteres")
        String motive,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate start_date,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate end_date
) {
}
