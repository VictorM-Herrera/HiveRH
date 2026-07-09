package com.HiveGroup.HiveRH.Features.Complaint;

import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintRequest;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintResponse;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {

    @Mapping(target = "id_complaint", ignore = true)
    @Mapping(source = "request.title", target = "title")
    @Mapping(source = "request.description", target = "description")
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "employee", target = "employee")
    ComplaintEntity toEntity(ComplaintRequest request, EmployeeEntity employee);

    @Mapping(source = "id_complaint", target = "idComplaint")
    @Mapping(source = "employee.id_employee", target = "idEmployee")
    @Mapping(target = "employeeName", expression = "java(getEmployeeName(complaint))")
    ComplaintResponse toResponse(ComplaintEntity complaint);

    default String getEmployeeName(ComplaintEntity complaint) {
        EmployeeEntity employee = complaint.getEmployee();
        return employee.getName() + " " + employee.getLastName();
    }
}
