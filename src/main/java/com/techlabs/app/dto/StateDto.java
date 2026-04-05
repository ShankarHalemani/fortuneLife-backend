package com.techlabs.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StateDto {
    private Long id;

    @Schema(example = "Maharashtra")
    private String name;

    private Boolean active;

    private Set<CityDto> cities;
}
