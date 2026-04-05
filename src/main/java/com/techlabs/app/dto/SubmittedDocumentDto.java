package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SubmittedDocumentDto {
	private Long id;

	@NotBlank
	@Schema(example = "Aadhaar Card")
	private String documentName;

	@Schema(example = "PENDING")
	private String documentStatus;

	@NotBlank
	@Schema(example = "https://cloudinary.com/document-url")
	private String documentImage;
}
