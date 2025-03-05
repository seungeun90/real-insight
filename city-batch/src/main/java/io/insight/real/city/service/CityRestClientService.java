package io.insight.real.city.service;

import io.insight.real.city.dto.CityBasicDto;
import io.insight.real.city.dto.request.CityBasicInfoRequest;
import io.insight.real.city.repository.entity.CityBasicInfo;
import io.insight.real.city.repository.jpa.CityBasicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * 공공데이터 1건 동기식 조회
 * */
@Slf4j
@RequiredArgsConstructor
@Service
public class CityRestClientService {

    private final RestClient restClient;
    private final AuthenticationService authService;
    private final CityBasicRepository repository;

    /**
     * 지역 정보 조회 요청이 있는 경우 단 건 조회
     * */
    public CityBasicDto getCityBasicInfo(CityBasicInfoRequest request) {
        CityBasicDto result = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats/population.json")
                     //  .queryParam("accessToken", authService.getAccessToken())
                        .queryParam("adm_cd", request.getAdmCd())
                        .queryParam("year", request.getYear())
                        .build())
                .retrieve()
                .body(CityBasicDto.class);

        CityBasicDto response = Objects.requireNonNull(result, "응답 객체가 null 입니다.");

        CityBasicInfo cityBasicInfo = CityBasicInfo.builder()
                .admCd(response.getAdmCd())
                .totalPopulation(response.getTotalPop())
                .averageAge(response.getAvgAge())
                .populationDensity(response.getDensity())
                .agingChildIndex(response.getAgedChildIdx())
                .householdCount(response.getFamily())
                .averageHouseholdSize(response.getAvgFamilyCnt())
                .build();

        CityBasicInfo byAdmCd = repository.findByAdmCdAndYear(request.getAdmCd(), request.getYear());
        if(byAdmCd == null) {
           repository.save(cityBasicInfo);
        }

        return response;
    }


}
