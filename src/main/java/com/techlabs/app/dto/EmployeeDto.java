package com.techlabs.app.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class EmployeeDto {

	@Schema(example = "1")
	private Long id;
	private Boolean active;

	@PositiveOrZero(message = "Amount Should be Greater than Zero")
	@Schema(example = "50000")
	private Double salary;

	private LocalDate joiningDate = LocalDate.now();
	private UserDto userDto;
}
