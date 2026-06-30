package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseFilterDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseReviewRequestDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/licenses")
@AllArgsConstructor
@Validated
@Tag(name = "Licenses", description = "Licencias de empleados y su estado de aprobacion.")
public class LicenseController {
    LicenseService licenseService;

    @GetMapping
    @Operation(summary = "Listar licencias", description = "Lista licencias en formato paginado y permite filtrar por estado, DNI del empleado, rango de fechas y si es paga.")
    public ResponseEntity<PageResponseDTO<LicenseDTO>> getLicenses(@Valid LicenseFilterDTO filters, Pageable pageable) {
        return ResponseEntity.ok().body(
                licenseService.getAllLicensePage(filters, pageable)
        );
    }

    @GetMapping("/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#id_license)")
    @Operation(summary = "Consultar licencia", description = "Obtiene una licencia por ID. La autorizacion valida acceso a la licencia solicitada.")
    public ResponseEntity<LicenseDTO> getLicenseByID(
            @P("id_license") @PathVariable @Positive(message = "El ID de la licencia debe ser mayor que cero") Long id_license) {
        return ResponseEntity.ok().body(licenseService.getLicense(id_license));
    }

    @PostMapping
    @Operation(summary = "Crear licencia", description = "Crea una licencia asociada al empleado autenticado y puede vincular certificados existentes.")
    public ResponseEntity<LicenseDTO> postLicense(@Valid @RequestBody RequestLicenseDTO license) {
        return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.createLicense(license));
    }

    @PatchMapping("/{id_license}")
    @Operation(summary = "Revisar licencia", description = "Permite a RRHH o ADMIN actualizar el estado y si la licencia es paga.")
    public ResponseEntity<LicenseDTO> reviewLicense(
            @PathVariable("id_license") @Positive(message = "El ID de la licencia debe ser mayor que cero") Long idLicense,
            @Valid @RequestBody LicenseReviewRequestDTO request) {
        return ResponseEntity.ok().body(licenseService.reviewLicense(idLicense, request));
    }

    @DeleteMapping("/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canDeleteLicense(#id_license)")
    @Operation(summary = "Eliminar licencia", description = "Elimina una licencia si el usuario autenticado tiene permisos sobre ella.")
    public ResponseEntity<Void> deleteLicense(
            @P("id_license") @PathVariable @Positive(message = "El ID de la licencia debe ser mayor que cero") Long id_license) {
        licenseService.deleteLicense(id_license);
        return ResponseEntity.noContent().build();
    }
}
