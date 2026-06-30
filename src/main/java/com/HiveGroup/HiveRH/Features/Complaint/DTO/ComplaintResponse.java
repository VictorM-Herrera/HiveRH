package com.HiveGroup.HiveRH.Features.Complaint.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.ComplaintStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record ComplaintResponse(
        Long idComplaint,
        String title,
        String description,
        LocalDate date,
        ComplaintStatusEnum status,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long idEmployee,
        String employeeName
) {
}
