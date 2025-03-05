package io.insight.real.dto;

import io.insight.real.infra.repository.entity.CityPopulation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class CityPopulationData {

    private List<CityPopulation> cityPopulation;
    private List<PopRankingData> popRankingData;
}
