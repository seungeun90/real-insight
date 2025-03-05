package io.insight.real.city.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PopRankingDto {
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

    public PopRankingDto(String provinceCode, String cityCode, String townCode, String townName) {
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.townCode = townCode;
        this.townName = townName;
    }

}
