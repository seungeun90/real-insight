package io.insight.real.service.out;

import io.insight.real.dto.PopRankingData;
import io.insight.real.dto.PopulationData;

import java.util.List;

public interface PopulationRepository {
    List<PopulationData> getPopulation(String provinceCode, String cityCode);
    List<PopRankingData> getPopulationRank(String provinceCode, String cityCode);

}
