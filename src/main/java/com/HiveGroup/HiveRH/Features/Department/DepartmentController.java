package com.HiveGroup.HiveRH.Features.Department;

import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentFilterDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentRequestDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentResponseDTO;
import com.HiveGroup.HiveRH.Features.Department.DTO.DepartmentStatusRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@AllArgsConstructor
@Validated
@Tag(name = "04 Departments", description = "Administracion de departamentos internos.")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Listar departamentos", description = "Obtiene departamentos y permite filtrar por ID, nombre y estado activo.")
    public ResponseEntity<List<DepartmentResponseDTO>> getDepartments(@ParameterObject @Valid DepartmentFilterDTO filters) {
        return ResponseEntity.ok(departmentService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Crear departamento", description = "Registra un nuevo departamento interno de la empresa.")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @PutMapping("/{id_department}")
    @Operation(summary = "Actualizar departamento", description = "Actualiza los datos principales de un departamento.")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(
            @NonNull @PathVariable("id_department") @Positive(message = "El ID del departamento debe ser mayor que cero") Long idDepartment,
            @Valid @RequestBody DepartmentRequestDTO request) {
        return ResponseEntity.ok(departmentService.updateById(idDepartment, request));
    }

    @PatchMapping("/{id_department}/status")
    @Operation(summary = "Cambiar estado de departamento", description = "Activa o desactiva un departamento mediante baja o alta logica.")
    public ResponseEntity<DepartmentResponseDTO> updateDepartmentStatus(
            @NonNull @PathVariable("id_department") @Positive(message = "El ID del departamento debe ser mayor que cero") Long idDepartment,
            @Valid @RequestBody DepartmentStatusRequestDTO request) {
        return ResponseEntity.ok(departmentService.updateStatus(idDepartment, request.active()));
    }
}
