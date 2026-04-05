package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestSchemeDto {
    private Long schemeId;

    @NotBlank
    @Schema(example = "Term Life Plan")
    private String schemeName;

    private Boolean active = true;

    private Long schemeDetailsId;

    @NotBlank
    @Schema(example = "https://via.placeholder.com/150")
    private String schemeImage;

    @NotBlank(message = "Description is required")
    @Schema(example = "A basic term life insurance plan with affordable premiums")
    private String description;

    @PositiveOrZero(message = "Minimum amount must be a non-negative number")
    @Schema(example = "50000")
    private Double minAmount;

    @PositiveOrZero(message = "Maximum amount must be a non-negative number")
    @Schema(example = "1000000")
    private Double maxAmount;

    @PositiveOrZero(message = "Minimum investment time must be a non-negative number")
    @Schema(example = "5")
    private Integer minInvestmentTime;

    @PositiveOrZero(message = "Maximum investment time must be a non-negative number")
    @Schema(example = "30")
    private Integer maxInvestmentTime;

    @PositiveOrZero(message = "Minimum age must be a non-negative number")
    @Schema(example = "18")
    private Integer minAge;

    @PositiveOrZero(message = "Maximum age must be a non-negative number")
    @Schema(example = "60")
    private Integer maxAge;

    @PositiveOrZero(message = "Profit ratio must be a non-negative number")
    @Schema(example = "10.0")
    private Double profitRatio;

    @PositiveOrZero(message = "Registration commission ratio must be a non-negative number")
    @Schema(example = "5.0")
    private Double registrationCommissionRatio;

    @PositiveOrZero(message = "Installment commission ratio must be a non-negative number")
    @Schema(example = "2.0")
    private Double installmentCommissionRatio;

    private Set<SchemeDocumentDto> documents = new HashSet<>();
}
