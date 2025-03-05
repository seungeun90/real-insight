package io.insight.real.city.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmploymentDto {
    private String provinceCode;
    private String cityCode;
    private String townCode;
    @JsonProperty("adm_cd")
    private String admCd;
    @JsonProperty("adm_nm")
    private String townName;
    @JsonProperty("tot_worker")
    private String employCnt; //종사자수
    @JsonProperty("corp_cnt")
    private String corpCnt; //사업체 수
    private String year;
}
