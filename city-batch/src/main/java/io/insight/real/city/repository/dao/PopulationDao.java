package io.insight.real.city.repository.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 인구 정보
 * 총 인구수와 연령대별 인구 비율,
 * 인구밀도, 평균 나이, 노령화 지수
 * https://sgisapi.kostat.go.kr/OpenAPI3/stats/population.json
 * https://sgisapi.kostat.go.kr/OpenAPI3/startupbiz/pplsummary.json  거주인구 요약정보
 * */
@Getter @Setter
@NoArgsConstructor
public class PopulationDao {
    private String admCd;
    private String teenageLessThanPer;
    private String teenagePer;
    private String twentyPer ;
    private String thirtyPer ;
    private String fortyPer ;
    private String fiftyPer ;
    private String sixtyPer ;
    private String seventyMoreThanPer;

    private String provinceCode;
    private String cityCode;
    private String townCode;

    private String townName;

    public PopulationDao(
            String provinceCode,
            String cityCode,
            String townCode,
            String townName,
            String teenageLessThanPer,
            String teenagePer,
            String twentyPer,
            String thirtyPer,
            String fortyPer,
            String fiftyPer,
            String sixtyPer,
            String seventyMoreThanPer
    ) {
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.townCode = townCode;
        this.townName = townName;
        this.teenageLessThanPer = teenageLessThanPer;
        this.teenagePer = teenagePer;
        this.twentyPer = twentyPer;
        this.thirtyPer = thirtyPer;
        this.fortyPer = fortyPer;
        this.fiftyPer = fiftyPer;
        this.sixtyPer = sixtyPer;
        this.seventyMoreThanPer = seventyMoreThanPer;
    }

}
