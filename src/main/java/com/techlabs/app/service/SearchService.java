package com.techlabs.app.service;

import com.techlabs.app.dto.SearchResultDto;

public interface SearchService {
    SearchResultDto search(String keyword);
}
