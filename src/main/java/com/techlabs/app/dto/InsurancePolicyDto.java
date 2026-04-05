package com.techlabs.app.dto;

import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InsurancePolicyDto {
	@NotBlank
	@Schema(example = "MONTHLY")
	private String premiumType;

	@PositiveOrZero(message = "Amount Should be Greater than Zero")
	@Schema(example = "100000")
	private Double policyAmount;

	@NotNull
	@Schema(example = "10")
	private Integer time;

	@PositiveOrZero(message = "Amount Should be Greater than Zero")
	@Schema(example = "1000")
	private Double premiumAmount;

	@Schema(example = "John Doe")
	private String nomineeName;

	@Schema(example = "Father")
	private String relationStatusWithNominee;

	private Set<SubmittedDocumentDto> submittedDocumentsDto = new HashSet<>();
}
