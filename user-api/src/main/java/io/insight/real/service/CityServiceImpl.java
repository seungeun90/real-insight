package io.insight.real.service;

import io.insight.real.dto.CityBasicData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.Employment;
import io.insight.real.service.in.CityService;
import io.insight.real.service.out.CityRankingSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CityServiceImpl implements CityService {
    private final CityRankingSearchRepository cityRankingRepository;

    public CityBasicData getCityData(String provinceCode, String cityCode, String year) {
        return cityRankingRepository.findCityData(provinceCode,cityCode,year);
    }

    public List<CityBasicInfo> getPopDataInPvc(String provinceCode, String year) {
        return cityRankingRepository.getCityPopInPvc(provinceCode,year);
    }
    public List<Employment> getCityWorkData(String provinceCode, String cityCode) {
        return cityRankingRepository.getWorkData(provinceCode,cityCode);
    }

    public CityRankingData getWorkRankingData(String provinceCode, String cityCode) {
        return cityRankingRepository.getWorkRankingData(provinceCode,cityCode);
    }

    public List<Employment> getWorkDataInPvc(String provinceCode){
        return cityRankingRepository.getWorkDataInPvc(provinceCode);
    }



}
