package com.HiveGroup.HiveRH.Features.Complaint;

import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintRequest;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintResponse;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintFilterDTO;
import com.HiveGroup.HiveRH.Features.Complaint.DTO.ComplaintStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@Tag(name = "12 Complaints", description = "Internal complaints and review tracking.")
public class ComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    @Operation(summary = "List complaints", description = "Returns internal complaints and supports filtering by ID, title, status, and date range.")
    public ResponseEntity<List<ComplaintResponse>> findAll(@ParameterObject ComplaintFilterDTO filters) {

        List<ComplaintResponse> response = complaintService.findAllByFilter(filters);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("@securityAuthorizationService.canCreateComplaintForEmployeeDni(#request.dni())")
    @Operation(summary = "Create complaint", description = "Registers a complaint associated with an active employee. New complaints start with PENDING status.")
    public ResponseEntity<ComplaintResponse> create(
            @P("request") @Valid @RequestBody ComplaintRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(complaintService.create(request));
    }


    @PutMapping("/{id_complaint}")
    @Operation(summary = "Update complaint status", description = "Updates a complaint status. Once reviewed, a complaint cannot be changed again.")
    public ResponseEntity<ComplaintResponse> updateStatus(
            @PathVariable("id_complaint") Long idComplaint,
            @Valid @RequestBody ComplaintStatusRequest request
    ) {

        ComplaintResponse response = complaintService.updateStatus(idComplaint, request);

        return ResponseEntity.ok(response);
    }
}
