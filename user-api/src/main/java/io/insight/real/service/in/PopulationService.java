package io.insight.real.service.in;

import io.insight.real.dto.CityBasicInfoData;
import io.insight.real.dto.CityPopulationData;

import java.util.List;

public interface PopulationService {
    CityPopulationData getPopulationDataInCity(String provinceCode, String cityCode);
    List<CityBasicInfoData> getPopDataInPvc(String provinceCode, String year);
}
