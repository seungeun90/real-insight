package io.insight.real.service;

import io.insight.real.dto.CityBasicInfoData;
import io.insight.real.dto.CityInfoData;
import io.insight.real.dto.CityEmploymentData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.service.in.CityService;
import io.insight.real.service.out.CityRankingSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CityServiceImpl implements CityService {
    private final CityRankingSearchRepository cityRankingRepository;

    public CityBasicInfoData getCityData(String admCd, String year) {
        return cityRankingRepository.getCityData(admCd, year);
    }

    public CityInfoData getCityData(String provinceCode, String cityCode, String year) {
        return cityRankingRepository.findCityData(provinceCode,cityCode,year);
    }

    public List<CityBasicInfoData> getPopDataInPvc(String provinceCode, String year) {
        return cityRankingRepository.getCityPopInPvc(provinceCode,year);
    }
    public List<CityEmploymentData> getCityWorkData(String provinceCode, String cityCode) {
        return cityRankingRepository.getWorkData(provinceCode,cityCode);
    }

    public CityRankingData getWorkRankingData(String provinceCode, String cityCode) {
        return cityRankingRepository.getWorkRankingData(provinceCode,cityCode);
    }

    public List<CityEmploymentData> getWorkDataInPvc(String provinceCode){
        return cityRankingRepository.getWorkDataInPvc(provinceCode);
    }



}
