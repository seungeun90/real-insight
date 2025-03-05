package io.insight.real.city.repository.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "city_basic_info")
public class CityBasicInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(name = "adm_cd", length = 50)
    private String admCd; // 시도 코드

    @Column(name = "province_code", length = 2)
    private String provinceCode; // 시도 코드

    @Column(name = "city_code", length = 3)
    private String cityCode; // 시도 코드

    @Column(name = "town_code", length = 10)
    private String townCode; // 시도 코드

    @Column(name = "total_population")
    private String totalPopulation; // 총 인구 수

    @Column(name = "average_age")
    private double averageAge; // 평균 나이

    @Column(name = "population_density")
    private double populationDensity; // 인구밀도

    @Column(name = "aging_child_index")
    private double agingChildIndex; // 노령화지수

    @Column(name = "household_count", length = 1000)
    private String householdCount; // 가구수

    @Column(name = "average_household_size")
    private double averageHouseholdSize; //평균가구원 수

    @Column(name = "employee_cnt")
    private String employCnt; //종사자수
    @Column(name = "corp_cnt")
    private String corpCnt; //사업체 수
    @Column(name = "year", length = 10)
    private String year;

}
