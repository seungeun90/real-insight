package io.insight.real.service.in;

import io.insight.real.dto.CityBasicData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.Employment;

import java.util.List;

public interface CityService {
    CityBasicInfo getCityData(String admCd, String year);
    CityBasicData getCityData(String provinceCode, String cityCode, String year);
    List<Employment> getCityWorkData(String provinceCode, String cityCode);
    CityRankingData getWorkRankingData(String provinceCode, String cityCode);
    List<Employment> getWorkDataInPvc(String provinceCode);
    List<CityBasicInfo> getPopDataInPvc(String provinceCode, String year);
}
