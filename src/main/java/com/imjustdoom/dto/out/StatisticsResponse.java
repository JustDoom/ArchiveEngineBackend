package com.imjustdoom.dto.out;

import java.util.List;

public record StatisticsResponse(List<DomainStatisticsResponse> statistics) {
}
