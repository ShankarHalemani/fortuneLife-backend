package com.techlabs.app.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ClaimDto {
    private Long id;

    @Schema(example = "POLICY_ID_HERE")
    private String policyId;
    private int totalInstallments;
    private int paidInstallments;

    @PositiveOrZero(message = "Amount Should be Greater than Zero")
    @Schema(example = "50000")
    private Double claimAmount;

    @NotBlank
    @Schema(example = "State Bank of India")
    private String bankName;

    @NotBlank
    @Schema(example = "Mumbai Main Branch")
    private String branchName;

    private String remarks;

    @NotBlank
    @Schema(example = "1234567890123")
    private String bankAccountNumber;

    @NotBlank
    @Schema(example = "SBIN0001234")
    private String ifscCode;

    private LocalDateTime date;
    private String claimStatus;
    private InsurancePolicyResponseDto policy;
}
