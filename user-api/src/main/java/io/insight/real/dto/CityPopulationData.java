package io.insight.real.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class CityPopulationData {

    private List<PopulationData> cityPopulation;
    private List<PopRankingData> popRankingData;


}
