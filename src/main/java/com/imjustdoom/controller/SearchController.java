package com.imjustdoom.controller;

import com.imjustdoom.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final UrlService urlService;

    @GetMapping()
    public ResponseEntity<?> search(@RequestParam("query") String query,
                                    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
                                    @RequestParam(value = "sortBy", required = false, defaultValue = "timestamp") String sortBy,
                                    @RequestParam(value = "ascending", required = false, defaultValue = "true") String ascending) {
        return this.urlService.searchChecks(query, page, sortBy, ascending);
    }
}
