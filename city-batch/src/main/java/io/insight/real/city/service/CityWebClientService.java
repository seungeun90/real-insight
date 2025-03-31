package io.insight.real.city.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.insight.real.city.dto.CityBasicDto;
import io.insight.real.city.dto.EmploymentDto;
import io.insight.real.city.dto.PopulationDto;
import io.insight.real.city.dto.request.CityBasicInfoRequest;
import io.insight.real.city.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 외부 API 조회
 * */
@Slf4j
@RequiredArgsConstructor
@Service
public class CityWebClientService {
    private final CommonWebClientService commonWebClientService;
    private final AuthenticationService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 지역정보 다 건 조회 API 호출
     * retry 정책 : access token 만료 시 token 재발급 후 retry 1회
     * @param requests : 조회할 지역 코드 목록
     * @return Flux<CityBasicDto> : 지역 정보 dto 목록
     * */
    public Flux<CityBasicDto> fetchCityBasicInfos(List<CityBasicInfoRequest> requests) {
         String url = "/stats/population.json";
         return Flux.fromIterable(requests)
                .delayElements(Duration.ofMillis(200))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(request -> {
                    Function<ApiResponse, Flux<CityBasicDto>> convertApiResponse = response -> {
                        List<CityBasicDto> list = Optional.ofNullable(
                                objectMapper.convertValue(response.getResult(), new TypeReference<List<CityBasicDto>>() {})
                        ).orElse(Collections.emptyList());

                        list.forEach(dto -> dto.setYear(request.getYear()));
                        return Flux.fromIterable(list);
                     };

                    BodyInserters.FormInserter<String> body = BodyInserters.fromFormData("accessToken", authService.getAccessToken())
                             .with("adm_cd", request.getAdmCd())
                             .with("year", request.getYear());

                    return commonWebClientService.executeRequest(url, ApiResponse.class, body, convertApiResponse)
                             .retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1))
                                     .filter(this::isAccessTokenExpiredError) // AccessToken 만료 시 재시도
                                     .doBeforeRetryAsync(retrySignal -> {
                                         log.warn("AccessToken이 만료됨, 새로 갱신 후 재시도...");
                                         return Mono.fromRunnable(authService::getAccessToken) // 새로운 AccessToken 즉시 갱신
                                                 .then();
                                     })
                             );
                 });
    }

    public Flux<EmploymentDto> fetchCityEmpInfos(List<CityBasicInfoRequest> requests) {
        String url = "/stats/company.json";
        return Flux.fromIterable(requests)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(request -> {
                    Function<ApiResponse, Flux<EmploymentDto>> convertApiResponse = response -> {
                        List<EmploymentDto> list = Optional.ofNullable(objectMapper.convertValue(response.getResult(),
                                new TypeReference<List<EmploymentDto>>() {})).orElse(Collections.emptyList());

                        if (list.isEmpty()) {
                            log.warn("변환된 EmploymentDto 리스트가 비어 있습니다. 요청 AdmCd: {}", request.getAdmCd());
                        }
                        list.forEach(dto -> {
                            String admCd = dto.getAdmCd();
                            String provinceCode = admCd.length() >= 2 ? admCd.substring(0, 2) : "";
                            String cityCode = admCd.length() >= 5 ? admCd.substring(2, 5) : "";
                            String townCode = admCd.length() > 5 ? admCd.substring(5) : "";
                            dto.setProvinceCode(provinceCode);
                            dto.setCityCode(cityCode);
                            dto.setTownCode(townCode);
                            dto.setYear(request.getYear());
                        }); // 추가 데이터 설정

                        return Flux.fromIterable(list);
                    };
                    BodyInserters.FormInserter<String> body = BodyInserters.fromFormData("accessToken", authService.getAccessToken())
                            .with("adm_cd", request.getAdmCd())
                            .with("year", request.getYear());

                    return commonWebClientService.executeRequest(url, ApiResponse.class, body, convertApiResponse)
                            .retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1))
                                    .filter(this::isAccessTokenExpiredError) // AccessToken 만료 시 재시도
                                    .doBeforeRetryAsync(retrySignal -> {
                                        log.warn("AccessToken이 만료됨, 새로 갱신 후 재시도...");
                                        return Mono.fromRunnable(authService::getAccessToken) // 새로운 AccessToken 즉시 갱신
                                                .then();
                                    })
                            );
                });
    }

    /**
     * 인구정보 다 건 조회 API 호출
     * retry 정책 : access token 만료 시 token 재발급 후 retry 1회
     * @param requests : 조회할 지역 코드 목록
     * @return Flux<PopulationDto> : 지역 정보 dto 목록
     * */
    public Flux<PopulationDto> fetchPopInfo(List<CityBasicInfoRequest> requests) {
        String url = "/startupbiz/pplsummary.json";
        return Flux.fromIterable(requests)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(request -> {
                    Function<ApiResponse, Flux<PopulationDto>> convertApiResponse = response -> {
                        List<PopulationDto> list = Optional.ofNullable(objectMapper.convertValue(response.getResult(),
                                new TypeReference<List<PopulationDto>>() {})).orElse(Collections.emptyList());

                        if (list.isEmpty()) {
                            log.warn("변환된 PopulationDto 리스트가 비어 있습니다. 요청 AdmCd: {}", request.getAdmCd());
                        }

                        return Flux.fromIterable(list)
                                .filter(dto -> dto.getAdmCd().equals(request.getAdmCd())); // request의 admCd와 비교
                    };
                    BodyInserters.FormInserter<String> body = BodyInserters.fromFormData("accessToken", authService.getAccessToken())
                            .with("adm_cd", request.getAdmCd());
                    return commonWebClientService.executeRequest(url,ApiResponse.class, body, convertApiResponse)
                            .retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1))
                                    .filter(this::isAccessTokenExpiredError) // AccessToken 만료 시 재시도
                                    .doBeforeRetryAsync(retrySignal -> {
                                        log.warn("AccessToken이 만료됨, 새로 갱신 후 재시도...");
                                        return Mono.fromRunnable(authService::getAccessToken) // 새로운 AccessToken 즉시 갱신
                                                .then();
                                    })
                            );
                });
    }

    /**
     * access token expired exception 확인
     * */
    private boolean isAccessTokenExpiredError(Throwable error) {
        if (error instanceof WebClientResponseException webClientError) {
            try {
                ApiResponse response = objectMapper.readValue(webClientError.getResponseBodyAsString(), ApiResponse.class);
                return response.getErrCd() == -401 ;
            } catch (Exception e) {
                log.error("API 응답 파싱 실패: {}", e.getMessage());
            }
        }
        return false;
    }



}
