package io.insight.real.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CityInfoData {

    private CityBasicInfoData cityBasicInfo;
    private CityRankingData cityRanking;

}
