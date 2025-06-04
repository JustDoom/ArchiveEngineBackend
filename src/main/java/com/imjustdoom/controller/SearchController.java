package com.imjustdoom.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SearchController {
//    private final UrlService urlService;
//
//    public SearchController(UrlService urlService) {
//        this.urlService = urlService;
//    }

//    @GetMapping("/search")
//    public ResponseEntity<?> search(@RequestParam("query") String query,
//                                    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
//                                    @RequestParam(value = "sortBy", required = false, defaultValue = "timestamp") String sortBy,
//                                    @RequestParam(value = "ascending", required = false, defaultValue = "true") String ascending) {
//        return this.urlService.searchChecks(query, page, sortBy, ascending);
//    }
//
//    @GetMapping("/statistics")
//    public ResponseEntity<?> statistics() {
//        return this.urlService.getStatistics();
//    }
}
