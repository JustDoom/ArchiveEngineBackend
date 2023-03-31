package com.imjustdoom.dto.out;

import com.imjustdoom.model.Domain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StatisticsResponse {

    private List<DomainStatisticsResponse> statistics;
}
