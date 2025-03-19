package io.insight.real.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CityDto {
    private String cityName;
    private String cityCode;
    private List<TownDto> towns;
}
