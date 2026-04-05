package com.techlabs.app.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class WithdrawalDto {

	private Long withdrawalId;

	private LocalDateTime withdrawalRequestDate;

	@NotNull
	@PositiveOrZero(message = "Amount Should be Greater than Zero")
	@Schema(example = "5000")
	private Double amount;

	@Schema(example = "Withdrawal approved")
	private String remarks;

	private String status;
	private Double leftCommission;
	private AgentDto agentDto;
}
