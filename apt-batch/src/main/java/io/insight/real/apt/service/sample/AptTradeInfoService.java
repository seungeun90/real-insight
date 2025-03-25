package io.insight.real.apt.service.sample;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.insight.real.apt.dto.response.ApartmentItem;
import io.insight.real.apt.dto.response.XmlSuccessResponse;
import io.insight.real.apt.dto.response.AptTradeProfit;
import io.insight.real.apt.repository.r2dbc.entity.AptTrade;
import io.insight.real.apt.repository.jpa.AptTradeQueryRepository;
import io.insight.real.apt.repository.mapper.AptTradeMapper;
import io.insight.real.apt.repository.r2dbc.AptTradeRepository;
import io.insight.real.apt.service.CommonWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AptTradeInfoService {
    private final CommonWebClientService webClientService;
    private final XmlMapper xmlMapper;
    private final AptTradeRepository aptTradeRepository;
    private final AptTradeMapper aptTradeMapper;
    private final AptTradeQueryRepository aptTradeQueryRepository;

    public List<AptTradeProfit> getAptTradeInfo(String regionCode, String areaSize){
        return aptTradeQueryRepository.findMaxMinPrices(regionCode,areaSize);
    }
    public void updateAptTradeInfo(String regionCode, String startDate, String endDate){
        if(!StringUtils.hasText(startDate)){
            throw new IllegalArgumentException("startDate cannot be null");
        }

        if(!StringUtils.hasText(endDate)) {
            endDate = startDate;
        }
        List<String> monthList = getMonthList(startDate, endDate);
        Flux.fromIterable(monthList)
                .flatMap(month -> fetchAllPages(buildUrl(1, regionCode, month)), 3) // 최대 3개 동시 실행
                .doOnTerminate(() -> System.out.println("모든 요청과 DB 저장 완료"))
                .subscribe();

    }

    public Flux<Void> fetchAllPages(URI url) {
        return webClientService.executeXmlRequest(url, BodyInserters.empty(), this::parseResponse)
                .flatMap(xmlSuccessResponse -> {
                    int totalCount = xmlSuccessResponse.getBody().getTotalCount();
                    int numOfRows = xmlSuccessResponse.getBody().getNumOfRows();
                    int totalPages = (int) Math.ceil((double) totalCount / numOfRows);
                    Flux<ApartmentItem> firstPageItems = Flux.fromIterable(xmlSuccessResponse.getBody().getItems());

                    Flux<ApartmentItem> otherPagesItems = Flux.range(2, totalPages)
                            .flatMap(pageNo -> webClientService.executeXmlRequest(buildUrl(pageNo, url), BodyInserters.empty(), this::parseResponse))
                            .flatMap(response -> Flux.fromIterable(response.getBody().getItems()));

                    return firstPageItems.mergeWith(otherPagesItems);
                })
                .buffer(100)
                .flatMap(this::saveBatchToDatabase)
                ;
    }
    private Mono<Void> saveBatchToDatabase(List<ApartmentItem> items) {
        List<AptTrade> entities = aptTradeMapper.toEntities(items);
        return aptTradeRepository.saveAll(entities)
            //    .doOnNext(saved -> log.info("저장 완료: {}", saved))
             //   .doOnError(error -> log.error("DB 저장 오류: {}", error.getMessage(), error))
                .then();
    }

    private Flux<XmlSuccessResponse> parseResponse(String response) {
        try {
            XmlSuccessResponse xmlSuccessResponse = xmlMapper.readValue(response, XmlSuccessResponse.class);
            return Flux.just(xmlSuccessResponse);
        } catch (Exception e) {
            log.error("XML 파싱 오류", e);
            return Flux.empty();
        }
    }
    private URI buildUrl(int pageNo, String regionCode, String dealDate) {
        String baseUrl = "https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade";
        String serviceKey = "gJFPGFZmoaEcP4T%2BMZpkkl%2BK50fCQUWgHpz8LBeSXx4VliUacXRUr5o%2FvxLiUYF9AoGANN%2BGbEpiDLpYZEdu2g%3D%3D";
        int numOfRows = 20;
        URI uri = URI.create(baseUrl
                + "?LAWD_CD="+regionCode
                + "&DEAL_YMD="+dealDate
                + "&serviceKey="+serviceKey
                + "&pageNo=" +pageNo
                + "&numOfRows="+numOfRows);
        return uri;
    }

    private URI buildUrl(int pageNo, URI originalUrl){
        String url = null;
        try {
            url = originalUrl.toURL().toString();
            return new URI(url.replaceAll("pageNo=\\d+", "pageNo=" + pageNo));
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<String> getMonthList(String startDate, String endDate) {
        // startDate와 endDate를 YearMonth 형식으로 변환
        YearMonth start = parseYearMonth(startDate);
        YearMonth end = parseYearMonth(endDate);

        List<String> monthList = new ArrayList<>();
        while (!start.isAfter(end)) {
            monthList.add(start.toString().replace("-", "")); // "yyyyMM" 형식으로 저장
            start = start.plusMonths(1);
        }
        return monthList;
    }

    private static YearMonth parseYearMonth(String date) {
        date = date.replace(".", ""); // "."을 제거하여 "yyyyMM" 형식으로 정리
        return YearMonth.of(
                Integer.parseInt(date.substring(0, 4)), // 연도
                Integer.parseInt(date.substring(4, 6))  // 월
        );
    }
}
