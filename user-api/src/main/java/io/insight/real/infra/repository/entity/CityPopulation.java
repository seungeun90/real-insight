package io.insight.real.infra.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Document(collection = "population")
public class CityPopulation {
    @Id
    private Long id; // 기본 키
    private String admCd;
    private String provinceCode; // 시도 코드
    private String cityCode; // 시도 코드
    private String townCode;
    private String townName;
    private String teenageLessThanPer;
    private String teenageLessThanCnt;
    private String teenagePer;
    private String teenageCnt;
    private String twentyPer ;
    private String twentyCnt ;
    private String thirtyPer ;
    private String thirtyCnt ;
    private String fortyPer ;
    private String fortyCnt ;
    private String fiftyPer ;
    private String fiftyCnt ;
    private String sixtyPer ;
    private String sixtyCnt ;
    private String seventyMoreThanPer;
    private String seventyMoreThanCnt;
}
