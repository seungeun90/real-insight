package io.insight.real.service.in;

import io.insight.real.dto.CityBasicInfoData;
import io.insight.real.dto.CityInfoData;
import io.insight.real.dto.CityEmploymentData;
import io.insight.real.dto.CityRankingData;

import java.util.List;

public interface CityService {
    CityBasicInfoData getCityData(String admCd, String year);
    CityInfoData getCityData(String provinceCode, String cityCode, String year);
    List<CityEmploymentData> getCityWorkData(String provinceCode, String cityCode);
    CityRankingData getWorkRankingData(String provinceCode, String cityCode);
    List<CityEmploymentData> getWorkDataInPvc(String provinceCode);
    List<CityBasicInfoData> getPopDataInPvc(String provinceCode, String year);
}
