package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {
    @Schema(example = "customer@example.com")
    private String to;

    @Schema(example = "Policy Confirmation")
    private String subject;

    @Schema(example = "Your policy has been confirmed successfully.")
    private String body;
}
