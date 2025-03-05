package io.insight.real.service;

import io.insight.real.dto.CityPopulationData;
import io.insight.real.service.out.PopulationRepository;
import io.insight.real.service.in.PopulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PopulationServiceImpl implements PopulationService {

    private final PopulationRepository populationRepository;
    @Override
    public CityPopulationData getPopulationDataInCity(String provinceCode, String cityCode) {
        return populationRepository.getPopulationDataInCity(provinceCode,cityCode);
    }
}
