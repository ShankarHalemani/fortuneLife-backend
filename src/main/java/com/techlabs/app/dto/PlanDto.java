package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDto {
    private Long id;

    @NotBlank
    @Schema(example = "Life Insurance")
    private String planName;

    private Boolean active;
}
