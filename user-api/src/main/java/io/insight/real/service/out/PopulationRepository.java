package io.insight.real.service.out;

import io.insight.real.dto.CityPopulationData;

public interface PopulationRepository {
    CityPopulationData getPopulationDataInCity(String provinceCode, String cityCode);
}
