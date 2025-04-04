package io.insight.real.service.in;

import io.insight.real.dto.CityPopulationData;
import io.insight.real.infra.repository.entity.CityBasicInfo;

import java.util.List;

public interface PopulationService {
    CityPopulationData getPopulationDataInCity(String provinceCode, String cityCode);
    List<CityBasicInfo> getPopDataInPvc(String provinceCode, String year);
}
