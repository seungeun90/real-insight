package io.insight.real.apt.dto.in;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class XmlErrorHeader {
    @JacksonXmlProperty(localName = "errMsg")
    private String errMsg;

    @JacksonXmlProperty(localName = "returnAuthMsg")
    private String returnAuthMsg;

    @JacksonXmlProperty(localName = "returnReasonCode")
    private String returnReasonCode;
}