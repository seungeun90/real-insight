package io.insight.real.infra.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CityPopulation that = (CityPopulation) o;
        return Objects.equals(id, that.id) && Objects.equals(admCd, that.admCd) && Objects.equals(provinceCode, that.provinceCode) && Objects.equals(cityCode, that.cityCode) && Objects.equals(townCode, that.townCode) && Objects.equals(townName, that.townName) && Objects.equals(teenageLessThanPer, that.teenageLessThanPer) && Objects.equals(teenageLessThanCnt, that.teenageLessThanCnt) && Objects.equals(teenagePer, that.teenagePer) && Objects.equals(teenageCnt, that.teenageCnt) && Objects.equals(twentyPer, that.twentyPer) && Objects.equals(twentyCnt, that.twentyCnt) && Objects.equals(thirtyPer, that.thirtyPer) && Objects.equals(thirtyCnt, that.thirtyCnt) && Objects.equals(fortyPer, that.fortyPer) && Objects.equals(fortyCnt, that.fortyCnt) && Objects.equals(fiftyPer, that.fiftyPer) && Objects.equals(fiftyCnt, that.fiftyCnt) && Objects.equals(sixtyPer, that.sixtyPer) && Objects.equals(sixtyCnt, that.sixtyCnt) && Objects.equals(seventyMoreThanPer, that.seventyMoreThanPer) && Objects.equals(seventyMoreThanCnt, that.seventyMoreThanCnt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, admCd, provinceCode, cityCode, townCode, townName, teenageLessThanPer, teenageLessThanCnt, teenagePer, teenageCnt, twentyPer, twentyCnt, thirtyPer, thirtyCnt, fortyPer, fortyCnt, fiftyPer, fiftyCnt, sixtyPer, sixtyCnt, seventyMoreThanPer, seventyMoreThanCnt);
    }
}
