package com.HiveGroup.HiveRH.Features.License.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.LicenseStatusEnum;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDTO {
    private Long id;
    private LocalDate requestDate;
    private LicenseStatusEnum status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPaid;
    private String motive;
    private String description;
    private List<Long> idCertificates;
    private String dniEmployee;
}
