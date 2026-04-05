package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class UserDto {
	@Schema(example = "1")
	private Long id;

	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z0-9])(?=.*[@$!%*#?&])[A-Za-z0-9@$!%*#?&]+$", message = "Username must contain alphanumeric characters and at least one special character.")
	@Schema(example = "john@admin1")
	private String username;

	@NotBlank
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "Password must be at least 8 characters long, and contain one uppercase letter, one lowercase letter, one number, and one special character.")
	@Schema(example = "Admin@1234")
	private String password;

	@NotBlank
	@Schema(example = "John")
	private String firstName;

	@Pattern(regexp = "Male|Female|Others|MALE|FEMALE|OTHERS", message = "Gender must be 'Male', 'Female', or 'Others'.")
	@Schema(example = "MALE")
	private String gender;

	private Boolean active;

	@Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only alphabetic characters.")
	@Schema(example = "Doe")
	private String lastName;

	@NotBlank
	@Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be a valid 10-digit number.")
	@Schema(example = "9876543210")
	private String mobileNumber;

	@NotBlank
	@Email(message = "Email should be in a proper format.")
	@Schema(example = "john@example.com")
	private String email;

	@Past(message = "Date of birth cannot be in the future.")
	@Schema(example = "1990-05-15")
	private LocalDate dateOfBirth;

	private AddressDto addressDto;
}
