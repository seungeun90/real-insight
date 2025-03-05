package io.insight.real.service.in;

import io.insight.real.dto.CityPopulationData;

public interface PopulationService {
    CityPopulationData getPopulationDataInCity(String provinceCode, String cityCode);
}
