package io.insight.real.city.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class RankedInfoDto {
    private String provinceCode;
    private String cityCode;
    private String year;
    private int totalPopRank;
    private int popDensityRank;
    private int agedChildIdxRank;
    private int familyCntRank;
    private int avgFamilyCntRank;
    private int employCntRank;
    private int corpCntRank;

    public RankedInfoDto(String provinceCode, String cityCode, String year) {
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.year = year;
    }

}