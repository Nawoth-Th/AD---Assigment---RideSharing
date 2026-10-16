package com.ridelink.account.dto;

import com.ridelink.account.model.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStatusRequest {

    @NotNull(message = "Status is required (ACTIVE, SUSPENDED)")
    @Schema(description = "Account operational status", example = "SUSPENDED")
    private AccountStatus status;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(AccountStatus status) {
        this.status = status;
    }
}
