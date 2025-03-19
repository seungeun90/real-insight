package io.insight.real.city.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ProvinceDto {
    private String _id;
    private String provinceName;
    private List<CityDto> cities;
}
