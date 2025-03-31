package io.insight.real.apt.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class XmlApartmentItem {

    @JacksonXmlProperty(localName = "aptDong")
    private String aptDong;

    @JacksonXmlProperty(localName = "aptNm")
    private String aptNm;

    @JacksonXmlProperty(localName = "buildYear")
    private int buildYear;

    @JacksonXmlProperty(localName = "buyerGbn")
    private String buyerGbn;

    @JacksonXmlProperty(localName = "dealAmount")
    private String dealAmount;

    @JacksonXmlProperty(localName = "dealDay")
    private int dealDay;

    @JacksonXmlProperty(localName = "dealMonth")
    private int dealMonth;

    @JacksonXmlProperty(localName = "dealYear")
    private int dealYear;

    @JacksonXmlProperty(localName = "dealingGbn")
    private String dealingGbn;

    @JacksonXmlProperty(localName = "cdealType")
    private String cdealType;

    @JacksonXmlProperty(localName = "cdealDay")
    private String cdealDay;

    @JacksonXmlProperty(localName = "estateAgentSggNm")
    private String estateAgentSggNm;

    @JacksonXmlProperty(localName = "excluUseAr")
    private double excluUseAr;

    @JacksonXmlProperty(localName = "floor")
    private int floor;

    @JacksonXmlProperty(localName = "jibun")
    private String jibun;

    @JacksonXmlProperty(localName = "landLeaseholdGbn")
    private String landLeaseholdGbn;

    @JacksonXmlProperty(localName = "rgstDate")
    private String rgstDate;

    @JacksonXmlProperty(localName = "sggCd")
    private String sggCd;

    @JacksonXmlProperty(localName = "slerGbn")
    private String slerGbn;

    @JacksonXmlProperty(localName = "umdNm")
    private String umdNm;
}
