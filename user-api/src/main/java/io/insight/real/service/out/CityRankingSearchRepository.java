package io.insight.real.service.out;

import io.insight.real.dto.CityBasicData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.Employment;

import java.util.List;

public interface CityRankingSearchRepository {
    CityBasicInfo getCityData(String admCd, String year);
    CityBasicData findCityData(String provinceCode, String cityCode, String year);
    List<CityBasicInfo> getCityPopInPvc(String provinceCode, String year);
    List<Employment> getWorkData(String provinceCode, String cityCode);
    CityRankingData getWorkRankingData(String provinceCode, String cityCode);
    List<Employment> getWorkDataInPvc(String provinceCode);
}