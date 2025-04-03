package io.insight.real.infra.repository.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Document(collection = "city_basic")
public class CityBasicInfo {
    @Id
    private Long id; // 기본 키
    private String admCd; // 시도 코드
    private String provinceCode; // 시도 코드

    private String cityCode;
    private String cityName;

    private String totalPopulation; // 총 인구 수

    private double averageAge; // 평균 나이

    private double populationDensity; // 인구밀도

    private String agingChildIndex; // 노령화지수

    private String householdCount; // 가구수

    private double averageHouseholdSize; //평균가구원 수

    private String employCnt; //종사자수
    private String corpCnt; //사업체 수
    private String year;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CityBasicInfo that = (CityBasicInfo) o;
        return Double.compare(averageAge, that.averageAge) == 0 && Double.compare(populationDensity, that.populationDensity) == 0 && Double.compare(averageHouseholdSize, that.averageHouseholdSize) == 0 && Objects.equals(id, that.id) && Objects.equals(admCd, that.admCd) && Objects.equals(provinceCode, that.provinceCode) && Objects.equals(cityCode, that.cityCode) && Objects.equals(cityName, that.cityName) && Objects.equals(totalPopulation, that.totalPopulation) && Objects.equals(agingChildIndex, that.agingChildIndex) && Objects.equals(householdCount, that.householdCount) && Objects.equals(employCnt, that.employCnt) && Objects.equals(corpCnt, that.corpCnt) && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, admCd, provinceCode, cityCode, cityName, totalPopulation, averageAge, populationDensity, agingChildIndex, householdCount, averageHouseholdSize, employCnt, corpCnt, year);
    }
}
