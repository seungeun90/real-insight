package io.insight.real.city.repository.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Employment {
    private String provinceCode;
    private String cityCode;
    private String townCode;
    private String admCd;
    private String townName;
    private String employCnt; //종사자수
    private String corpCnt; //사업체 수
    private String year;

  /*  private String year;

    *//**
     * 1단계 하위 행정구역 정보 요청 : 1
     * 2단계 하위 행정구역 정보 요청 : 2
     * *//*
    private String lowSearch;


    private String classCode;//산업분류API조회항목

    *//**
     * 백화점/중대형마트 9001
     * 병원 9003
     * *//*
    private String themeCd;*/



}
