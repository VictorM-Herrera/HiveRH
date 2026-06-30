package com.HiveGroup.HiveRH.Features.Complaint;

import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintRequest;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintResponse;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintFilterDTO;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaints", description = "Denuncias internas y seguimiento de revision.")
public class ComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    @Operation(summary = "Listar denuncias", description = "Lista denuncias internas y permite filtrar por ID, titulo, estado y rango de fechas.")
    public ResponseEntity<List<ComplaintResponse>> findAll(ComplaintFilterDTO filters) {

        List<ComplaintResponse> response = complaintService.findAllByFilter(filters);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("@securityAuthorizationService.canCreateComplaintForEmployeeDni(#request.dni())")
    @Operation(summary = "Crear denuncia", description = "Registra una denuncia asociada a un empleado activo. Al crearse queda en estado PENDING.")
    public ResponseEntity<ComplaintResponse> create(
            @P("request") @Valid @RequestBody ComplaintRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(complaintService.create(request));
    }


    @PutMapping("/{id_complaint}")
    @Operation(summary = "Actualizar estado de denuncia", description = "Actualiza el estado de una denuncia. Una denuncia revisada no puede volver a modificarse.")
    public ResponseEntity<ComplaintResponse> updateStatus(
            @PathVariable("id_complaint") Long idComplaint,
            @Valid @RequestBody ComplaintStatusRequest request
    ) {

        ComplaintResponse response = complaintService.updateStatus(idComplaint, request);

        return ResponseEntity.ok(response);
    }
}
