package com.imjustdoom.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResponse {

    private List<SimpleUrlResponse> urls;
}
