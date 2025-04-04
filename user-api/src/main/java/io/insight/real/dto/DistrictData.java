package io.insight.real.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class DistrictData {
    private String id; // 기본 키
    private String provinceName;
    private List<CityData> cities;
}
