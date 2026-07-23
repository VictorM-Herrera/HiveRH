package com.HiveGroup.HiveRH.Features.Account;

import com.HiveGroup.HiveRH.Features.Account.DTO.ResponseAccountDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountEmailDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountPasswordDTO;
import com.HiveGroup.HiveRH.Features.Account.DTO.UpdateAccountRoleDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Validated
@Tag(name = "02 Accounts", description = "Authenticated account operations and role management.")
public class AccountController {
    private final AccountService accountService;

    @PatchMapping("/{email}/role")
    @Operation(summary = "Update account role", description = "Changes the role of an existing account. Used to manage access permissions across the system.")
    public ResponseEntity<ResponseAccountDTO> updateRole(
            @PathVariable String email,
            @Valid @RequestBody UpdateAccountRoleDTO request) {
        return ResponseEntity.ok(accountService.updateRole(email, request.rol()));
    }

    @PatchMapping("/{dni}/rol")
    @Operation(summary = "Update account role by employee DNI", description = "Changes the role of the account linked to the provided employee DNI.")
    public ResponseEntity<ResponseAccountDTO> updateRoleDNI(
            @PathVariable @Positive(message = "El ID de la cuenta debe ser mayor que cero") String dni,
            @Valid @RequestBody UpdateAccountRoleDTO request) {
        return ResponseEntity.ok(accountService.updateRoleDNI(dni, request.rol()));
    }

    @PatchMapping("/me/email")
    @Operation(summary = "Update my email", description = "Updates the email address of the account associated with the authenticated token.")
    public ResponseEntity<ResponseAccountDTO> updateMyEmail(@Valid @RequestBody UpdateAccountEmailDTO request) {
        return ResponseEntity.ok(accountService.updateCurrentEmail(request.email()));
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Update my password", description = "Updates the authenticated account password after validating the current password.")
    public ResponseEntity<ResponseAccountDTO> updateMyPassword(@Valid @RequestBody UpdateAccountPasswordDTO request) {
        return ResponseEntity.ok(accountService.updateCurrentPassword(
                request.currentPassword(),
                request.newPassword()
        ));
    }
}
