package com.techlabs.app.controller;

import com.techlabs.app.dto.SearchResultDto;
import com.techlabs.app.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fortuneLife/search")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @Operation(summary = "Global search across customers, agents, policies, and schemes")
    @GetMapping
    public ResponseEntity<SearchResultDto> search(@RequestParam String keyword) {
        logger.info("Global search for keyword: {}", keyword);
        SearchResultDto results = searchService.search(keyword);
        return ResponseEntity.ok(results);
    }
}
