package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDate.class)
public interface VacationMapper {

    @Mapping(target = "id_vacation", ignore = true)
    @Mapping(target = "requestDate", expression = "java(request.requestDate() != null ? request.requestDate() : LocalDate.now())")
    @Mapping(source = "request.accepted", target = "accepted")
    @Mapping(source = "request.startDate", target = "startDate")
    @Mapping(source = "request.endDate", target = "endDate")
    @Mapping(source = "request.paid", target = "paid")
    @Mapping(source = "employee", target = "employee")
    VacationEntity toEntity(VacationRequest request, EmployeeEntity employee);

    @Mapping(source = "id_vacation", target = "idVacation")
    @Mapping(source = "employee.dni", target = "dniEmployee")
    @Mapping(target = "employeeName", expression = "java(getEmployeeName(vacation))")
    VacationResponse toResponse(VacationEntity vacation);

    List<VacationResponse> toResponseList(List<VacationEntity> vacations);

    default String getEmployeeName(VacationEntity vacation) {
        EmployeeEntity employee = vacation.getEmployee();
        return employee.getName() + " " + employee.getLastName();
    }
}
