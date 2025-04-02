package io.insight.real.apt.dto.in;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JacksonXmlRootElement(localName = "OpenAPI_ServiceResponse")
public class XmlErrorResponse implements XmlResponse {

    @JacksonXmlProperty(localName = "cmmMsgHeader")
    private XmlErrorHeader cmmMsgHeader;
}