package com.HiveGroup.HiveRH.Features.Suspension;

import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionFilterDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionRequestDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suspensions")
@AllArgsConstructor
@Tag(name = "13 Suspensions", description = "Employee suspensions and related status changes.")
public class SuspensionController {
    private final SuspensionService suspensionService;

    @GetMapping
    @Operation(summary = "List suspensions", description = "Returns suspensions and supports the available filters.")
    public ResponseEntity<List<SuspensionResponseDTO>> getSuspensions(@ParameterObject @Valid SuspensionFilterDTO filters) {
        return ResponseEntity.ok(suspensionService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Create suspension", description = "Registers a suspension for an employee and automatically changes their status to SUSPENDED.")
    public ResponseEntity<SuspensionResponseDTO> createSuspension(@Valid @RequestBody SuspensionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suspensionService.create(request));
    }
}
