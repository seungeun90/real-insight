package io.insight.real.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class CityData {
    private String cityCode;
    private String cityName;
    private List<String> towns;
}
