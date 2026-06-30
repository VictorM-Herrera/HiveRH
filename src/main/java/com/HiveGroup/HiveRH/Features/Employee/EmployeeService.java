package com.HiveGroup.HiveRH.Features.Employee;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Common.Utils.TextSearchUtils;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Branch.BranchEntity;
import com.HiveGroup.HiveRH.Features.Branch.BranchRepository;
import com.HiveGroup.HiveRH.Features.Department.DepartamentRepository;
import com.HiveGroup.HiveRH.Features.Department.DepartmentEntity;
import com.HiveGroup.HiveRH.Features.Employee.DTO.*;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentDTO;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import com.HiveGroup.HiveRH.Features.Position.PositionEntity;
import com.HiveGroup.HiveRH.Features.Position.PositionRepository;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final AccountRepository accountRepository;
    private final PositionRepository positionRepository;
    private final DepartamentRepository departamentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmployeeResponseDTO create(EmployeeCreateDTO employeeCreateDTO) {

        validateDniAvailable(employeeCreateDTO.dni());
        validateAdult(employeeCreateDTO.birth_date());

        if (employeeCreateDTO.id_branch() == null) {
            throw new EntityNotFoundException("La sucursal es obligatoria", "Branch");
        }

        if (employeeCreateDTO.id_position() == null) {
            throw new EntityNotFoundException("El puesto es obligatorio", "Position");
        }

        if (employeeCreateDTO.id_department() == null) {
            throw new EntityNotFoundException("El departamento es obligatorio", "Department");
        }

        BranchEntity branch = branchRepository.findById(employeeCreateDTO.id_branch())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada", "Branch"));

        PositionEntity position = positionRepository.findById(employeeCreateDTO.id_position())
                .orElseThrow(() -> new EntityNotFoundException("Puesto no encontrado", "Position"));

        DepartmentEntity department = departamentRepository.findById(employeeCreateDTO.id_department())
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado", "Department"));

        EmployeeEntity employee = EmployeeEntity.builder()
                .name(employeeCreateDTO.name())
                .lastName(employeeCreateDTO.lastName())
                .phoneNumber(employeeCreateDTO.phoneNumber())
                .genre(employeeCreateDTO.genre())
                .dni(employeeCreateDTO.dni())
                .city(employeeCreateDTO.city())
                .address(employeeCreateDTO.address())
                .birthdate(employeeCreateDTO.birth_date())
                .hireDate(employeeCreateDTO.hire_date())
                .baseSalary(employeeCreateDTO.base_salary())
                .status(StatusEnum.ACTIVE)
                .branch(branch)
                .build();

        EmployeeAssignmentEntity assignment = new EmployeeAssignmentEntity();
        assignment.setEmployee(employee);
        assignment.setPosition(position);
        assignment.setDepartment(department);

        List<EmployeeAssignmentEntity> assignments = new ArrayList<>();
        assignments.add(assignment);
        employee.setAssignments(assignments);

        EmployeeEntity createdEmployee = employeeRepository.save(employee);

        AccountEntity defaultAccount = createDefaultAccount(createdEmployee);
        createdEmployee.setAccount(defaultAccount);

        createdEmployee = employeeRepository.save(createdEmployee);

        return toDTO(createdEmployee);
    }

    @Transactional
    public EmployeeResponseDTO deleteByDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }

        EmployeeEntity employee = employeeRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado para el DNI indicado", "Employee"));

        employee.setStatus(StatusEnum.TERMINATED);
        if (employee.getAccount() != null) {
            AccountEntity account = employee.getAccount();
            account.setStatusEnum(StatusEnum.TERMINATED);
            accountRepository.save(account);
        }

        EmployeeEntity deletedEmployee = employeeRepository.save(employee);

        return toDTO(deletedEmployee);
    }

    @Transactional
    public EmployeeResponseDTO putByDni(String dni, EmployeeUpdateDTO employeeUpdateDTO) {

        EmployeeEntity employee = findEmployeeByDni(dni);

        validateUniqueDni(employeeUpdateDTO.dni(), employee.getId_employee());
        validateAdult(employeeUpdateDTO.birth_date());

        employee.setName(employeeUpdateDTO.name());
        employee.setLastName(employeeUpdateDTO.lastName());
        employee.setPhoneNumber(employeeUpdateDTO.phoneNumber());
        employee.setGenre(employeeUpdateDTO.genre());
        employee.setDni(employeeUpdateDTO.dni());
        employee.setCity(employeeUpdateDTO.city());
        employee.setAddress(employeeUpdateDTO.address());
        employee.setBirthdate(employeeUpdateDTO.birth_date());
        employee.setHireDate(employeeUpdateDTO.hire_date());
        employee.setTerminationDate(employeeUpdateDTO.termination_date());
        employee.setStatus(employeeUpdateDTO.status());
        employee.setBaseSalary(employeeUpdateDTO.base_salary());

        BranchEntity branch = branchRepository.findById(employeeUpdateDTO.id_branch())
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada", "Branch"));

        employee.setBranch(branch);

        EmployeeEntity updatedEmployee = employeeRepository.save(employee);

        return toDTO(updatedEmployee);
    }

    @Transactional
    public EmployeeResponseDTO patchByDni(String dni, EmployeePatchDTO employeePatchDTO) {

        EmployeeEntity employee = findEmployeeByDni(dni);

        if (employeePatchDTO.dni() != null) {
            validateUniqueDni(employeePatchDTO.dni(), employee.getId_employee());
        }

        if (employeePatchDTO.birth_date() != null) {
            validateAdult(employeePatchDTO.birth_date());
        }

        employee.setName(employeePatchDTO.name() != null ? employeePatchDTO.name() : employee.getName());
        employee.setLastName(employeePatchDTO.lastName() != null ? employeePatchDTO.lastName() : employee.getLastName());
        employee.setPhoneNumber(employeePatchDTO.phoneNumber() != null ? employeePatchDTO.phoneNumber() : employee.getPhoneNumber());
        employee.setGenre(employeePatchDTO.genre() != null ? employeePatchDTO.genre() : employee.getGenre());
        employee.setDni(employeePatchDTO.dni() != null ? employeePatchDTO.dni() : employee.getDni());
        employee.setCity(employeePatchDTO.city() != null ? employeePatchDTO.city() : employee.getCity());
        employee.setAddress(employeePatchDTO.address() != null ? employeePatchDTO.address() : employee.getAddress());
        employee.setBirthdate(employeePatchDTO.birth_date() != null ? employeePatchDTO.birth_date() : employee.getBirthdate());
        employee.setHireDate(employeePatchDTO.hire_date() != null ? employeePatchDTO.hire_date() : employee.getHireDate());
        employee.setTerminationDate(employeePatchDTO.termination_date() != null ? employeePatchDTO.termination_date() : employee.getTerminationDate());
        employee.setStatus(employeePatchDTO.status() != null ? employeePatchDTO.status() : employee.getStatus());
        employee.setBaseSalary(employeePatchDTO.base_salary() != null ? employeePatchDTO.base_salary() : employee.getBaseSalary());

        if (employeePatchDTO.id_branch() != null) {
            BranchEntity branch = branchRepository.findById(employeePatchDTO.id_branch())
                    .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada", "Branch"));

            employee.setBranch(branch);
        }

        EmployeeEntity updatedEmployee = employeeRepository.save(employee);

        return toDTO(updatedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO findByDni(String dni) {
        EmployeeEntity employee = findEmployeeByDni(dni);

        return toDTO(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO findCurrentEmployee() {
        AccountEntity account = getCurrentAccount();

        EmployeeEntity employee = employeeRepository.findByAccount(account).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado para la cuenta autenticada", "Employee"));

        loadEmployeeAssignments(employee);

        return toDTO(employee);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<EmployeeResponseDTO> findAllByFilter(EmployeeFilterDTO filters, Pageable pageable) {
        EmployeeFilterDTO activeFilters = filters != null
                ? filters
                : new EmployeeFilterDTO(null, null, null, null, null, null, null, null, null, null);

        List<EmployeeEntity> employeeList = employeeRepository.findAll();

        List<EmployeeResponseDTO> filteredEmployees = employeeList.stream()
                .map(this::toDTO)
                .filter(employee -> TextSearchUtils.matchesFullName(employee.name(), employee.lastName(), activeFilters.fullName()))
                .filter(employee -> activeFilters.dni() == null || employee.dni().equals(activeFilters.dni()))
                .filter(employee -> activeFilters.id_branch() == null || employee.branch_id().equals(activeFilters.id_branch()))
                .filter(employee -> activeFilters.hire_date() == null || employee.hireDate().equals(activeFilters.hire_date()))
                .filter(employee -> activeFilters.termination_date() == null || employee.terminationDate().equals(activeFilters.termination_date()))
                .filter(employee -> activeFilters.status() == null || employee.status() == activeFilters.status())
                .filter(employee -> activeFilters.position() == null || employee.assignments().stream().anyMatch(a -> a.positionName().equalsIgnoreCase(activeFilters.position())))
                .filter(employee -> activeFilters.department() == null || employee.assignments().stream().anyMatch(a -> a.departmentName().equalsIgnoreCase(activeFilters.department())))
                .filter(employee -> activeFilters.min_salary() == null || employee.baseSalary() >= activeFilters.min_salary())
                .filter(employee -> activeFilters.max_salary() == null || employee.baseSalary() <= activeFilters.max_salary())
                .toList();

        return toPageResponse(filteredEmployees, pageable);
    }

    private EmployeeResponseDTO toDTO(EmployeeEntity employee) {
        List<EmployeeAssignmentDTO> assignments = employee.getAssignments() == null
                ? List.of()
                : employee.getAssignments().stream()
                .map(assignment -> new EmployeeAssignmentDTO(
                        assignment.getDepartment().getId_department(),
                        assignment.getDepartment().getDepartmentName(),
                        assignment.getPosition().getId_position(),
                        assignment.getPosition().getPositionName()
                ))
                .toList();

        return new EmployeeResponseDTO(
                employee.getName(),
                employee.getLastName(),
                employee.getPhoneNumber(),
                employee.getGenre(),
                employee.getDni(),
                employee.getCity(),
                employee.getAddress(),
                employee.getBirthdate(),
                employee.getHireDate(),
                employee.getTerminationDate(),
                employee.getBaseSalary(),
                employee.getStatus(),
                employee.getBranch().getId_branch(),
                employee.getAccount() != null ? employee.getAccount().getId_account() : null,
                assignments
        );
    }

    private AccountEntity createDefaultAccount(EmployeeEntity employee) {
        String dni = employee.getDni();

        AccountEntity account = AccountEntity.builder()
                .user(dni)
                .email(dni + "@hiverh.com")
                .password(passwordEncoder.encode(dni))
                .rol(RolEnum.EMPLOYEE)
                .statusEnum(StatusEnum.ACTIVE)
                .build();

        return accountRepository.save(account);
    }

    private void validateDniAvailable(String dni) {
        validateUniqueDni(dni, null);

        if (accountRepository.findByUserOrEmail(dni, dni + "@hiverh.com").isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta asociada a ese DNI");
        }
    }

    private EmployeeEntity findEmployeeByDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }

        return employeeRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado para el DNI indicado", "Employee"));
    }

    private void validateAdult(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }

        if (Period.between(birthDate, LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("El empleado debe tener al menos 18 años");
        }
    }

    private void validateUniqueDni(String dni, Long currentEmployeeId) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }

        employeeRepository.findByDni(dni)
                .filter(employee ->
                        currentEmployeeId == null
                                || !employee.getId_employee().equals(currentEmployeeId)
                )
                .ifPresent(employee -> {
                    throw new IllegalArgumentException(
                            "Ya existe un empleado registrado con el DNI " + dni
                    );
                });
    }

    private AccountEntity getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AccountEntity account) {
            return account;
        }

        String username = authentication.getName();

        return accountRepository.findByUserOrEmail(username, username)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cuenta inexistente",
                        "AccountEntity"
                ));
    }

    private PageResponseDTO<EmployeeResponseDTO> toPageResponse(List<EmployeeResponseDTO> employees, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return new PageResponseDTO<>(
                    employees,
                    0,
                    employees.size(),
                    employees.size(),
                    employees.isEmpty() ? 0 : 1
            );
        }

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, employees.size());
        List<EmployeeResponseDTO> content = start >= employees.size()
                ? List.of()
                : employees.subList(start, end);

        int totalPages = (int) Math.ceil((double) employees.size() / size);

        return new PageResponseDTO<>(
                content,
                page,
                size,
                employees.size(),
                totalPages
        );
    }

    private void loadEmployeeAssignments(EmployeeEntity employee) {
        if (employee.getAssignments() != null) {
            Hibernate.initialize(employee.getAssignments());

            employee.getAssignments().forEach(assignment -> {
                Hibernate.initialize(assignment.getDepartment());
                Hibernate.initialize(assignment.getPosition());
            });
        }
    }
}
