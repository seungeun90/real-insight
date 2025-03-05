package io.insight.real.city.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "city_population")
public class CityPopulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키
    @Column(name ="adm_cd")
    private String admCd;
    @Column(name = "province_code", length = 2)
    private String provinceCode; // 시도 코드
    @Column(name = "city_code", length = 3)
    private String cityCode; // 시도 코드
    @Column(name = "town_code", length = 3)
    private String townCode; // 시도 코드
    @Column(name = "town_name", length = 20)
    private String townName; // 시도 코드
    @Column(name ="teenage_less_than_per")
    private String teenageLessThanPer;
    @Column(name ="teenage_less_than_cnt")
    private String teenageLessThanCnt;
    @Column(name ="teenage_per")
    private String teenagePer;
    @Column(name ="teenage_cnt")
    private String teenageCnt;
    @Column(name ="twenty_per")
    private String twentyPer ;
    @Column(name ="twenty_cnt")
    private String twentyCnt ;
    @Column(name ="thirty_per")
    private String thirtyPer ;
    @Column(name ="thirty_cnt")
    private String thirtyCnt ;
    @Column(name ="forty_per")
    private String fortyPer ;
    @Column(name ="forty_cnt")
    private String fortyCnt ;
    @Column(name ="fifty_per")
    private String fiftyPer ;
    @Column(name ="fifty_cnt")
    private String fiftyCnt ;
    @Column(name ="sixty_per")
    private String sixtyPer ;
    @Column(name ="sixty_cnt")
    private String sixtyCnt ;
    @Column(name ="seventy_more_than_per")
    private String seventyMoreThanPer;
    @Column(name ="seventy_more_than_cnt")
    private String seventyMoreThanCnt;
}
