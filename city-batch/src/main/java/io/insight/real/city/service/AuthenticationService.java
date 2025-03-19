package io.insight.real.city.service;

import io.insight.real.city.config.properties.ApiProperties;
import io.insight.real.city.dto.SgisToken;
import io.insight.real.city.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 공공데이터 API 사용 시 필요한 access token 관리
 * */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final RestClient restClient;
    private final AtomicReference<SgisToken> ATOMIC_TOKEN = new AtomicReference<>(null);
    private final ApiProperties apiProperties;

    public String getAccessToken()  {
        String cachedToken = null;
        if(ATOMIC_TOKEN.get() != null) {
            SgisToken token = ATOMIC_TOKEN.get();
            cachedToken = token.getAccessToken();
            if (cachedToken != null && !token.isTokenExpired()) {
                return cachedToken;
            }
        }

        synchronized (this) {
            if(ATOMIC_TOKEN.get() != null) {
                SgisToken token = ATOMIC_TOKEN.get();
                cachedToken = token.getAccessToken();
                if (cachedToken != null && !token.isTokenExpired()) {
                    return cachedToken;
                }
            }
            log.info("Fetching new access token...");
            ApiResponse apiResponse = getAuthentication();

            if (apiResponse.hasFailed()) {
                log.info("Authentication failed : errorCode={},message={} ", apiResponse.getErrCd(), apiResponse.getErrMsg());
                throw new RuntimeException(apiResponse.getErrMsg());
            }

            Map<String, Object> data = (Map<String, Object>) apiResponse.getResult();
            cachedToken = (String) data.get("accessToken");
            long expiresIn = Long.parseLong(String.valueOf(data.get("accessTimeout")));
            SgisToken updated = new SgisToken(cachedToken, Instant.ofEpochMilli(expiresIn));
            ATOMIC_TOKEN.set(updated);
            log.info("cache completed new access token...{}", cachedToken);
        }
        return cachedToken;
    }

    public ApiResponse getAuthentication(){
        log.info("Fetching access token...key={}, secret={}", apiProperties.getKey(), apiProperties.getSecret());
        try {
            return restClient
                    .method(HttpMethod.GET)
                    .uri(uriBuilder -> uriBuilder
                            .path("/auth/authentication.json")
                            .queryParam("consumer_key", apiProperties.getKey())
                            .queryParam("consumer_secret", apiProperties.getSecret())
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse>() {});
        } catch (HttpStatusCodeException ex) {
            log.error("ex={}", ex.getCause());
            throw new RuntimeException(ex.getMessage());
        // return ApiFailResponse.fromHttpException(ex, entity.getBody());
        } catch (Exception ex) {
            log.info("ex={}", ex.getCause());
            log.info("{}", ex.getMessage());
            log.info("{}", ex.getClass());
            throw new RuntimeException(ex.getMessage());
        }
    }


}
