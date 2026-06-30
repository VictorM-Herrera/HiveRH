package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SuspensionFilterDTO(

        @Pattern(
                regexp = "^\\d{7,8}$",
                message = "El DNI debe contener 7 u 8 números, sin puntos, letras ni espacios"
        )
        String dniEmployee,

        LocalDate start_date,

        LocalDate end_date,

        @Size(max = 100, message = "El nombre completo no puede superar los 100 caracteres")
        String fullName
) {
}
