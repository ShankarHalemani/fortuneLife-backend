package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class AddressDto {
	private Long id;

	@Schema(example = "42")
	private String houseNumber;

	@Schema(example = "Sunshine Apartments")
	private String apartment;

	@Schema(example = "Mumbai")
	private String city;

	@Schema(example = "Maharashtra")
	private String state;

	@Schema(example = "400001")
	private Integer pinCode;
}
