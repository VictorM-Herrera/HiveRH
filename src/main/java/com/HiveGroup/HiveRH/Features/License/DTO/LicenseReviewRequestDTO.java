package com.HiveGroup.HiveRH.Features.License.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.LicenseStatusEnum;
import jakarta.validation.constraints.NotNull;

public record LicenseReviewRequestDTO(
        @NotNull(message = "El estado de la licencia es obligatorio")
        LicenseStatusEnum status,

        @NotNull(message = "Debe indicar si la licencia es paga")
        Boolean isPaid
) {
}
