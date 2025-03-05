package io.insight.real.city.service.out;

import io.insight.real.city.dto.CityBasicDto;

import java.util.List;

public interface CityInfoSearchRepository {
    List<CityBasicDto> groupByAdmCd(String code, String year);
}
