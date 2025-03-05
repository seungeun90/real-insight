package io.insight.real.city.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopulationDto {
    @JsonProperty("adm_cd")
    private String admCd;
    @JsonProperty("adm_nm")
    private String admNm;
    @JsonProperty("teenage_less_than_per")
    private String teenageLessThanPer;
    @JsonProperty("teenage_less_than_cnt")
    private String teenageLessThanCnt;
    @JsonProperty("teenage_per")
    private String teenagePer;
    @JsonProperty("teenage_cnt")
    private String teenageCnt;
    @JsonProperty("twenty_per")
    private String twentyPer ;
    @JsonProperty("twenty_cnt")
    private String twentyCnt ;
    @JsonProperty("thirty_per")
    private String thirtyPer ;
    @JsonProperty("thirty_cnt")
    private String thirtyCnt ;
    @JsonProperty("forty_per")
    private String fortyPer ;
    @JsonProperty("forty_cnt")
    private String fortyCnt ;
    @JsonProperty("fifty_per")
    private String fiftyPer ;
    @JsonProperty("fifty_cnt")
    private String fiftyCnt ;
    @JsonProperty("sixty_per")
    private String sixtyPer ;
    @JsonProperty("sixty_cnt")
    private String sixtyCnt ;
    @JsonProperty("seventy_more_than_per")
    private String seventyMoreThanPer;
    @JsonProperty("seventy_more_than_cnt")
    private String seventyMoreThanCnt;

    @JsonProperty("province_code")
    private String provinceCode;
    @JsonProperty("city_code")
    private String cityCode;
    @JsonProperty("town_code")
    private String townCode;

    public PopulationDto(
            String provinceCode,
            String cityCode,
            String townCode,
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
