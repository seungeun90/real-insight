package io.insight.real.infra.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


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

}
