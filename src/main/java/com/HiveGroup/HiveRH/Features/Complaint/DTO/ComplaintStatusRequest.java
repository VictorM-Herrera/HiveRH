package com.HiveGroup.HiveRH.Features.Complaint.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.ComplaintStatusEnum;
import jakarta.validation.constraints.NotNull;

public record ComplaintStatusRequest(

        @NotNull(message = "El estado es obligatorio")
        ComplaintStatusEnum status
) {
}