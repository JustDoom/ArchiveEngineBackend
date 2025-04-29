package com.imjustdoom.dto.out;

import java.util.List;

public record SearchResponse(List<SimpleUrlResponse> urls) {
}
