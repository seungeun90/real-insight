package io.insight.real.service;

import io.insight.real.dto.CityBasicData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.Employment;
import io.insight.real.infra.repository.jpa.CityDataRepository;
import io.insight.real.infra.repository.jpa.DistrictRepository;
import io.insight.real.service.out.CityRankingSearchRepository;
import io.insight.real.service.in.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CityServiceImpl implements CityService {
    private final CityDataRepository cityDataRepository;
    private final CityRankingSearchRepository cityRankingRepository;
    private final DistrictRepository districtRepository;



    public CityBasicInfo getCityBasicInfo(
            String provinceCode,String cityCode, String year) {
        String admcd = provinceCode + cityCode ;
        if(StringUtils.isEmpty(year)) {
            year = "2023";
        }
        return cityDataRepository.getCityBasicInfoByAdmCdAndYear(admcd,year);
    }

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
