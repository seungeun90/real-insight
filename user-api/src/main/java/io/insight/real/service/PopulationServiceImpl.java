package io.insight.real.service;

import io.insight.real.dto.CityBasicInfoData;
import io.insight.real.dto.CityPopulationData;
import io.insight.real.dto.PopRankingData;
import io.insight.real.dto.PopulationData;
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

    public List<CityBasicInfoData> getPopDataInPvc(String provinceCode, String year) {
        return cityService.getPopDataInPvc(provinceCode,year);
    }

    @Override
    public CityPopulationData getPopulationDataInCity(String provinceCode, String cityCode) {
        List<PopulationData> population = populationRepository.getPopulation(provinceCode, cityCode);
        population.forEach(data ->{
            CityBasicInfoData cityData = cityService.getCityData(data.getAdmCd(), "2023");
            data.setTotalPopulation(cityData.getTotalPopulation());
            data.setHouseholdCount(cityData.getHouseholdCount());
            data.setAverageHouseholdSize(cityData.getAverageHouseholdSize());
        });

        List<PopRankingData> populationRank = populationRepository.getPopulationRank(provinceCode, cityCode);

        return CityPopulationData.builder()
                .cityPopulation(population)
                .popRankingData(populationRank)
                .build();
    }
}
