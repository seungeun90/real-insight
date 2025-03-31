package io.insight.real.apt.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JacksonXmlRootElement(localName = "response")
public class XmlSuccessResponse implements XmlResponse {

    @JacksonXmlProperty(localName = "header")
    private XmlResponseHeader header;

    @JacksonXmlProperty(localName = "body")
    private XmlResponseBody body;


} 