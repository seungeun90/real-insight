package io.insight.real.apt.batch;

import io.insight.real.apt.dto.response.AptResponse;
import io.insight.real.apt.service.CommonWebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@StepScope
public class AptItemReader  implements ItemReader<URI> {
    private String adres;
    private int currentPage = 0;
    private int totalPages = 0;
    private final CommonWebClientService webClientService;

    public AptItemReader(
            CommonWebClientService webClientService,
            @Value("#{jobParameters['adres']}") String adres) {
        this.webClientService = webClientService;
        this.adres = adres;
    }
    @Override
    public URI read() {
        if(currentPage > totalPages){
            return null;
        }
        if(currentPage == 0){
            AptResponse response = webClientService.executeRequest(buildUrl(currentPage, adres), AptResponse.class, BodyInserters.empty(), Flux::just)
                    .blockFirst();
            if (response != null) {
                totalPages = (int) Math.ceil((double) response.getMatchCount() / response.getPerPage());
                log.info("✅ 총 페이지 수 설정: {}", totalPages);
            }
        }
        URI uri = buildUrl(currentPage, adres);
        currentPage++;
        return uri;
    }
    private URI buildUrl(int pageNo, String addr) {
        String baseUrl = "https://api.odcloud.kr/api/AptIdInfoSvc/v1/getAptInfo";
        String serviceKey = "gJFPGFZmoaEcP4T%2BMZpkkl%2BK50fCQUWgHpz8LBeSXx4VliUacXRUr5o%2FvxLiUYF9AoGANN%2BGbEpiDLpYZEdu2g%3D%3D";
        int numOfRows = 100;
        String encodedCond = URLEncoder.encode("cond[ADRES::LIKE]", StandardCharsets.UTF_8);
        String encodedAddr = URLEncoder.encode(addr, StandardCharsets.UTF_8).replace("+", "%20"); // 띄어쓰기`+`를 `%20`으로 변환

        return URI.create(baseUrl
                + "?perPage=" + numOfRows
                + "&returnType=JSON"
                + "&" + encodedCond + "=" + encodedAddr
                + "&serviceKey=" + serviceKey
                + "&page=" + pageNo);
    }
}
