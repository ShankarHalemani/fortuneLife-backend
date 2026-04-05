package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private String policyHolderName;
    private String paymentMethodId;

    @Schema(example = "POLICY_ID_HERE")
    private String policyId;

    private Long paymentId;
    private LocalDateTime dateOfPayment;

    @Schema(example = "debit")
    private String paymentType;

    @Schema(example = "5000")
    private Double amount;

    @Schema(example = "900")
    private Double tax;

    @Schema(example = "5900")
    private Double totalPayment;

    private String paymentStatus;
}
