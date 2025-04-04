package io.insight.real.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CityEmploymentData {
    private String id; // 기본 키
    private String provinceCode;
    private String cityCode;
    private String townCode;
    private String townName;
    private String employCnt; //종사자수
    private String corpCnt; //사업체 수
    private String year;
}
