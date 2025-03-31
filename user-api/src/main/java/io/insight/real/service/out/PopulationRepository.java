package io.insight.real.service.out;

import io.insight.real.dto.PopRankingData;
import io.insight.real.infra.repository.entity.CityPopulation;

import java.util.List;

public interface PopulationRepository {
    List<CityPopulation> getPopulation(String provinceCode, String cityCode);
    List<PopRankingData> getPopulationRank(String provinceCode, String cityCode);

}
