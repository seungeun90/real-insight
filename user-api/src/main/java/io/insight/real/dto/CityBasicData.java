package io.insight.real.dto;

import io.insight.real.infra.repository.entity.CityBasicInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CityBasicData {

    private CityBasicInfo cityBasicInfo;
    private CityRankingData cityRankingData;

}
