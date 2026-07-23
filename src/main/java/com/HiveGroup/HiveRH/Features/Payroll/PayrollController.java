package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollFilterDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@AllArgsConstructor
@Tag(name = "08 Payrolls", description = "Payroll records and employee payroll queries.")
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    @Operation(summary = "List payrolls", description = "Returns paginated payroll records. Requires ADMIN or RRHH role.")
    public ResponseEntity<PageResponseDTO<PayrollResponse>> getPayrolls(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(payrollService.getAllPage(pageable));
    }

    @GetMapping("/employee/{dni_employee}")
    @PreAuthorize("@securityAuthorizationService.canAccessEmployeeDni(#dniEmployee)")
    @Operation(summary = "List payrolls by employee", description = "Returns payroll records for an employee with date filters. Employees can only access their own payrolls.")
    public ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(
            @P("dniEmployee") @PathVariable("dni_employee") String dniEmployee,
            @ParameterObject PayrollFilterDTO filters
    ) {
        return ResponseEntity.ok(payrollService.findAllByEmployee(dniEmployee, filters));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    @Operation(summary = "Create payroll", description = "Generates a payroll by calculating base salary plus variations. Validates active employee status, salary rules, and one payroll per month.")
    public ResponseEntity<PayrollResponse> createPayroll(
            @Valid @RequestBody PayrollRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payrollService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    @Operation(summary = "Update payroll", description = "Updates an existing payroll and recalculates values from the submitted request.")
    public ResponseEntity<PayrollResponse> updatePayroll(
            @NonNull @PathVariable Long id,
            @Valid @RequestBody PayrollRequest request
    ) {
        return ResponseEntity.ok(payrollService.updateById(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    @Operation(summary = "Delete payroll", description = "Deletes a payroll record and returns the deleted data.")
    public ResponseEntity<PayrollResponse> deletePayroll(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.deleteById(id));
    }
}
