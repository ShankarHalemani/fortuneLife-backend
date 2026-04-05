package com.techlabs.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto {
    private List<SearchItemDto> customers;
    private List<SearchItemDto> agents;
    private List<SearchItemDto> policies;
    private List<SearchItemDto> schemes;
}
