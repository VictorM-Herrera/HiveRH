package com.HiveGroup.HiveRH.Features.Variation;

import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationFilterDTO;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationRequest;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variations")
@RequiredArgsConstructor
@Tag(name = "07 Variations", description = "Salary concepts that add to or subtract from payrolls.")
public class VariationController {

    private final VariationService variationService;

    @GetMapping("/{id}")
    @Operation(summary = "Get variation", description = "Returns a salary variation by ID.")
    public ResponseEntity<VariationResponse> getVariationById(@PathVariable Long id) {
        return ResponseEntity.ok(variationService.findById(id));
    }

    @GetMapping
    @Operation(summary = "List variations", description = "Returns salary variations and supports the available filters.")
    public ResponseEntity<List<VariationResponse>> getVariations(@ParameterObject VariationFilterDTO filters) {
        return ResponseEntity.ok(variationService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Create variation", description = "Creates a salary concept. A positive total increases payroll and a negative total deducts from it.")
    public ResponseEntity<VariationResponse> createVariation(
            @Valid @RequestBody VariationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(variationService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update variation", description = "Updates only the provided fields of a salary variation.")
    public ResponseEntity<VariationResponse> patchVariation(
            @PathVariable Long id,
            @RequestBody VariationRequest request
    ) {
        return ResponseEntity.ok(variationService.patchById(id, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace variation", description = "Updates the main data of an existing salary variation.")
    public ResponseEntity<VariationResponse> putVariation(
            @PathVariable Long id,
            @Valid @RequestBody VariationRequest request
    ) {
        return ResponseEntity.ok(variationService.putById(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete variation", description = "Deletes a salary variation.")
    public ResponseEntity<VariationResponse> deleteVariation(@PathVariable Long id) {
        return ResponseEntity.ok(variationService.deleteById(id));
    }
}
