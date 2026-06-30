package com.HiveGroup.HiveRH.Features.Suspension;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionRequestDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SuspensionMapper {

    @Mapping(target = "id_suspension", ignore = true)
    @Mapping(source = "employee", target = "employee")
    @Mapping(source = "request.motive", target = "motive")
    @Mapping(source = "request.start_date", target = "startDate")
    @Mapping(source = "request.end_date", target = "endDate")
    SuspensionEntity toEntity(SuspensionRequestDTO request, EmployeeEntity employee);

    @Mapping(source = "id_suspension", target = "id_suspension")
    @Mapping(source = "employee.dni", target = "dniEmployee")
    @Mapping(source = "employee.name", target = "employeeName")
    @Mapping(source = "employee.lastName", target = "employeeLastName")
    @Mapping(source = "startDate", target = "start_date")
    @Mapping(source = "endDate", target = "end_date")
    SuspensionResponseDTO toResponse(SuspensionEntity suspension);
}
