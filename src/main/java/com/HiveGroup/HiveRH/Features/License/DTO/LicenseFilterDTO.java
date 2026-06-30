package com.HiveGroup.HiveRH.Features.License.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.LicenseStatusEnum;

import java.time.LocalDate;

public record LicenseFilterDTO(
        LicenseStatusEnum status,
        String dniEmployee,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isPaid
) {
}
