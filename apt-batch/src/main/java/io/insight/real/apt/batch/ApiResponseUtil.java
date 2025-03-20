package io.insight.real.apt.batch;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.insight.real.apt.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiResponseUtil {

    private final XmlMapper xmlMapper;

    public Flux<ApiResponse> parseResponse(String response) {
        try {
            ApiResponse apiResponse = xmlMapper.readValue(response, ApiResponse.class);
            return Flux.just(apiResponse);
        } catch (Exception e) {
            log.error("XML 파싱 오류", e);
            return Flux.empty();
        }
    }
}
