package com.HiveGroup.HiveRH.Features.Suspension;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Common.Utils.TextSearchUtils;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionFilterDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionRequestDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionResponseDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class SuspensionService {
    private final SuspensionRepository suspensionRepository;
    private final EmployeeRepository employeeRepository;
    private final SuspensionMapper suspensionMapper;

    public List<SuspensionResponseDTO> findAllByFilter(SuspensionFilterDTO filters) {
        SuspensionFilterDTO activeFilters = filters != null
                ? filters
                : new SuspensionFilterDTO(null, null, null, null);

        return suspensionRepository.findAll().stream()
                .filter(suspension -> activeFilters.dniEmployee() == null || suspension.getEmployee().getDni().equals(activeFilters.dniEmployee()))
                .filter(suspension -> matchesDateRange(suspension, activeFilters))
                .filter(suspension -> TextSearchUtils.matchesFullName(
                        suspension.getEmployee().getName(),
                        suspension.getEmployee().getLastName(),
                        activeFilters.fullName()
                ))
                .map(suspensionMapper::toResponse)
                .toList();
    }

    @Transactional
    public SuspensionResponseDTO create(SuspensionRequestDTO request) {
        if (request.dniEmployee() == null || request.dniEmployee().isBlank()) {
            throw new IllegalArgumentException("El DNI del empleado es obligatorio");
        }
        if (request.motive() == null || request.motive().isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio");
        }
        if (request.start_date() == null || request.end_date() == null) {
            throw new IllegalArgumentException("Las fechas de suspensión son obligatorias");
        }
        if (request.end_date().isBefore(request.start_date())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        EmployeeEntity employee = employeeRepository.findByDni(request.dniEmployee())
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado para el DNI indicado", "Employee"));

        validateEmployeeCanBeSuspended(employee);

        SuspensionEntity suspension = suspensionMapper.toEntity(request, employee);

        employee.setStatus(StatusEnum.SUSPENDED);
        employeeRepository.save(employee);

        return suspensionMapper.toResponse(suspensionRepository.save(suspension));
    }

    @Transactional
    public void reactivateEmployeesWithExpiredSuspensions() {
        suspensionRepository.findByEndDateLessThanEqual(LocalDate.now())
                .stream()
                .map(SuspensionEntity::getEmployee)
                .filter(employee -> employee.getStatus() == StatusEnum.SUSPENDED)
                .forEach(employee -> employee.setStatus(StatusEnum.ACTIVE));
    }

    private boolean matchesDateRange(SuspensionEntity suspension, SuspensionFilterDTO filters) {
        if (filters.start_date() == null && filters.end_date() == null) {
            return true;
        }

        boolean startsBeforeFilterEnd = filters.end_date() == null || !suspension.getStartDate().isAfter(filters.end_date());
        boolean endsAfterFilterStart = filters.start_date() == null || !suspension.getEndDate().isBefore(filters.start_date());

        return startsBeforeFilterEnd && endsAfterFilterStart;
    }

    private void validateEmployeeCanBeSuspended(EmployeeEntity employee) {
        if (employee.getStatus() != StatusEnum.ACTIVE) {
            throw new IllegalArgumentException("Solo se puede suspender a empleados activos");
        }
    }
}
