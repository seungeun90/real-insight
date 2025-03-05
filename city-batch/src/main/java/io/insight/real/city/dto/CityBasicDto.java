package io.insight.real.city.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
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
public class CityBasicDto {
    @JsonProperty("adm_cd")
    private String admCd;
    @JsonProperty("adm_nm")
    private String admNm;
    private String year;
    @JsonProperty("tot_ppltn")
    private String totalPop; //총인구수
    @JsonProperty("avg_age")
    private double avgAge; //평균 연령
    @JsonProperty("ppltn_dnsty")
    private double density; //인구밀도
    @JsonProperty("aged_child_idx")
    private double agedChildIdx; //노령화지수,
    @JsonProperty("tot_family")
    private String family; //가구수
    @JsonProperty("avg_fmember_cnt")
    private double avgFamilyCnt; //평균 가구원 수

    @JsonProperty("employee_cnt")
    private String employCnt; //종사자수
    @JsonProperty("corp_cnt")
    private String corpCnt; //사업체 수

    private String provinceCode;
    private String cityCode;
    private String townCode;


    public CityBasicDto(
            String provinceCode,
            String cityCode,
            String year,
            String totalPopulation,
            double populationDensity,
            double agingChildIndex,
            String householdCount,
            double avgFamilyCnt,
            String employCnt,
            String corpCnt
    ) {
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.year = year;
        this.totalPop = totalPopulation;
        this.density = populationDensity;
        this.agedChildIdx = agingChildIndex;
        this.family = householdCount;
        this.avgFamilyCnt = avgFamilyCnt;
        this.employCnt = employCnt;
        this.corpCnt = corpCnt;
    }


}
