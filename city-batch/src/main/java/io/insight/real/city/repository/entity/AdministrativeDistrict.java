package io.insight.real.city.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "adm_district")
public class AdministrativeDistrict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(name = "province_code", length = 10)
    private String provinceCode; // 시도 코드

    @Column(name = "province_name", length = 255)
    private String provinceName; // 시도 명칭

    @Column(name = "city_district_code", length = 10)
    private String cityDistrictCode; // 시군구 코드

    @Column(name = "city_district_name", length = 255)
    private String cityDistrictName; // 시군구 명칭

    @Column(name = "town_code", length = 10)
    private String townCode; // 읍면동 코드

    @Column(name = "town_name", length = 255)
    private String townName; // 읍면동 명칭
}
