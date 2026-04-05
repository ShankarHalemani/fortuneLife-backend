package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AgentDto {

	@Schema(example = "1")
	private Long id;
	private Boolean active;
	private String image;
	private Boolean verified;
	private Double totalCommission = 0.0;

	@Pattern(regexp = "^[0-9]{10,15}$", message = "Account number must be between 10 and 15 digits")
	@Schema(example = "1234567890")
	private String accountNumber;

	@Size(max = 50, message = "Bank name must not exceed 50 characters")
	@Schema(example = "State Bank of India")
	private String bankName;

	@Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "IFSC code must be 11 characters: first four letters, fifth character must be '0', followed by six alphanumeric characters")
	@Schema(example = "SBIN0001234")
	private String ifscCode;

	private int totalTransactions;
	private UserDto userDto;
}
