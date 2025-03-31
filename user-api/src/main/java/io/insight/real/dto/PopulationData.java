package io.insight.real.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PopulationData {
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

    private String totalPopulation;
    private String householdCount; // 가구수
    private double averageHouseholdSize; //평균가구원 수
}
