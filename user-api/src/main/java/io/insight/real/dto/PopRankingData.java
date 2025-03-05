package io.insight.real.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PopRankingData {

    private String provinceCode;
    private String cityCode;
    private String townCode;
    private String townName;
    private int lessThanTeenPer;
    private int teenPer;
    private int twentyPer;
    private int thirtyPer;
    private int fortyPer;
    private int fiftyPer;
    private int sixtyPer;
    private int moreThanSevenPer;
    private int thirtyToFiftyPer;

    public static String getCollectionName() {
        return "pop_ranking";
    }
}
