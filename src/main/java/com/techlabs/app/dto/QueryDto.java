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
public class QueryDto {
    private Long id;

    @Schema(example = "Policy Inquiry")
    private String title;

    @Schema(example = "What is the maturity amount for Term Life Plan?")
    private String question;

    @Schema(example = "The maturity depends on policy amount and tenure.")
    private String answer;

    @Schema(example = "RESOLVED")
    private String queryResponse;

    private Boolean active;

    @Schema(example = "customer@example.com")
    private String email;
}
