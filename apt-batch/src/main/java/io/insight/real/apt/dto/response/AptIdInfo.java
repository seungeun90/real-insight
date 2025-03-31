package io.insight.real.apt.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AptIdInfo {

    @JsonProperty("ADRES")
    private String adres;

    @JsonProperty("COMPLEX_GB_CD")
    private String complexGbCd;

    @JsonProperty("COMPLEX_NM1")
    private String complexGbNm1;

    @JsonProperty("COMPLEX_NM2")
    private String complexGbNm2;

    @JsonProperty("COMPLEX_NM3")
    private String complexGbNm3;

    @JsonProperty("COMPLEX_PK")
    private String complexPk;

    @JsonProperty("DONG_CNT")
    private Integer dongCnt;

    @JsonProperty("PNU")
    private String pnu;

    @JsonProperty("UNIT_CNT")
    private Integer unitCnt;

    @JsonProperty("USEAPR_DT")
    private String useaprDt;
}
