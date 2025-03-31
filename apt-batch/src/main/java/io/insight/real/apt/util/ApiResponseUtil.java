package io.insight.real.apt.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.insight.real.apt.dto.response.XmlErrorResponse;
import io.insight.real.apt.dto.response.XmlSuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiResponseUtil {

    private final XmlMapper xmlMapper;

    public Flux<XmlSuccessResponse> parseResponse(String response) {
        try {
            if (response.contains("<response>")) {
                return Flux.just(xmlMapper.readValue(response, XmlSuccessResponse.class));
            }
            XmlErrorResponse errorResponse = xmlMapper.readValue(response, XmlErrorResponse.class);
            log.error("API 호출 오류 CODE={}, MSG={}", errorResponse.getCmmMsgHeader().getReturnReasonCode(), errorResponse.getCmmMsgHeader().getErrMsg());
            throw new RuntimeException(errorResponse.getCmmMsgHeader().getErrMsg());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

   /* public Flux<XmlSuccessResponse> parseResponse(String response) {
        try {
            XmlSuccessResponse xmlSuccessResponse = xmlMapper.readValue(response, XmlSuccessResponse.class);
            return Flux.just(xmlSuccessResponse);
        } catch (Exception e) {
            log.error("XML 파싱 오류", e);
            return Flux.empty();
        }
    }*/
}
