package io.insight.real.service;

import io.insight.real.dto.CityPopulationData;
import io.insight.real.dto.PopRankingData;
import io.insight.real.dto.PopulationData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.CityPopulation;
import io.insight.real.service.in.CityService;
import io.insight.real.service.in.PopulationService;
import io.insight.real.service.out.PopulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PopulationServiceImpl implements PopulationService {

    private final PopulationRepository populationRepository;
    private final CityService cityService;

    public List<CityBasicInfo> getPopDataInPvc(String provinceCode, String year) {
        return cityService.getPopDataInPvc(provinceCode,year);
    }

    @Override
    public CityPopulationData getPopulationDataInCity(String provinceCode, String cityCode) {
        List<CityPopulation> population = populationRepository.getPopulation(provinceCode, cityCode);
        List<PopulationData> populationDataList = new ArrayList<>();
        population.forEach(data ->{
            CityBasicInfo cityData = cityService.getCityData(data.getAdmCd(), "2023");
            PopulationData populationData = new PopulationData();
            populationData.setAdmCd(data.getAdmCd());
            populationData.setProvinceCode(data.getProvinceCode());
            populationData.setCityCode(data.getCityCode());
            populationData.setTownCode(data.getTownCode());
            populationData.setTownName(data.getTownName());
            populationData.setTeenageLessThanPer(data.getTeenageLessThanPer());
            populationData.setTeenageLessThanCnt(data.getTeenageLessThanCnt());
            populationData.setTeenagePer(data.getTeenagePer());
            populationData.setTeenageCnt(data.getTeenageCnt());
            populationData.setTwentyPer(data.getTwentyPer());
            populationData.setTwentyCnt(data.getTwentyCnt());
            populationData.setThirtyPer(data.getThirtyPer());
            populationData.setThirtyCnt(data.getThirtyCnt());
            populationData.setFortyPer(data.getFortyPer());
            populationData.setFortyCnt(data.getFortyCnt());
            populationData.setFiftyPer(data.getFiftyPer());
            populationData.setFiftyCnt(data.getFiftyCnt());
            populationData.setSixtyPer(data.getSixtyPer());
            populationData.setSixtyCnt(data.getSixtyCnt());
            populationData.setSeventyMoreThanPer(data.getSeventyMoreThanPer());
            populationData.setSeventyMoreThanCnt(data.getSeventyMoreThanCnt());

            populationData.setTotalPopulation(cityData.getTotalPopulation());
            populationData.setHouseholdCount(cityData.getHouseholdCount());
            populationData.setAverageHouseholdSize(cityData.getAverageHouseholdSize());

            populationDataList.add(populationData);
        });

        List<PopRankingData> populationRank = populationRepository.getPopulationRank(provinceCode, cityCode);


        return CityPopulationData.builder()
                .cityPopulation(populationDataList)
                .popRankingData(populationRank)
                .build();
    }
}
