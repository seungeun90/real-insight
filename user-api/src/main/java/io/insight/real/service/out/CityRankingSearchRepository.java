package io.insight.real.service.out;

import io.insight.real.dto.CityBasicInfoData;
import io.insight.real.dto.CityInfoData;
import io.insight.real.dto.CityEmploymentData;
import io.insight.real.dto.CityRankingData;

import java.util.List;

public interface CityRankingSearchRepository {
    CityBasicInfoData getCityData(String admCd, String year);
    CityInfoData findCityData(String provinceCode, String cityCode, String year);
    List<CityBasicInfoData> getCityPopInPvc(String provinceCode, String year);
    List<CityEmploymentData> getWorkData(String provinceCode, String cityCode);
    CityRankingData getWorkRankingData(String provinceCode, String cityCode);
    List<CityEmploymentData> getWorkDataInPvc(String provinceCode);
}