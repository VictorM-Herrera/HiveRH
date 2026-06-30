package com.HiveGroup.HiveRH.Features.Vacation.DTO;

import java.time.LocalDate;

public record VacationFilterDTO(
        Boolean accepted,
        LocalDate startDate,
        LocalDate endDate,
        String dniEmployee,
        String fullName
) {
}
