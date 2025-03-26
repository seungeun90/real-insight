package io.insight.real.apt.service;

import io.insight.real.apt.batch.ApiResponseUtil;
import io.insight.real.apt.batch.job.BatchJobStatusService;
import io.insight.real.apt.config.ApiProperties;
import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.response.*;
import io.insight.real.apt.repository.mapper.AptInfoMapper;
import io.insight.real.apt.repository.mapper.AptTradeMapper;
import io.insight.real.apt.repository.r2dbc.AptInfoCustomRepository;
import io.insight.real.apt.repository.r2dbc.AptTradeRepository;
import io.insight.real.apt.repository.r2dbc.entity.AptInfo;
import io.insight.real.apt.repository.r2dbc.entity.AptTrade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.YearMonth;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AptTradeJobService {
    private final CommonWebClientService webClientService;
    private final ApiProperties apiProperties;
    private final BatchJobStatusService batchJobStatusService;
    private final AptTradeRepository aptTradeRepository;
    private final AptTradeMapper aptTradeMapper;
    private final ApiResponseUtil apiResponseUtil;

    public void triggerJob(BatchJobRequest request){
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        String regionCode = request.getRegionCode();
        Long jobId = request.getJobId();
        String adres = request.getAdres();
        List<String> monthList = new ArrayList<>();

        if(!StringUtils.hasText(startDate)){
            throw new IllegalArgumentException("startDate cannot be null");
        }
        if(!StringUtils.hasText(endDate)) {
            endDate = startDate;
        }

        monthList = getMonthList(startDate, endDate);

        Flux.fromIterable(monthList)
            .delayElements(Duration.ofSeconds(1))
            .flatMap(month -> {
                // 페이지 수 조회 + runAptJob 수행
                return webClientService.executeXmlRequest(
                                buildUrl(1, regionCode, month),
                                BodyInserters.empty(),
                                apiResponseUtil::parseResponse
                        )
                        .switchIfEmpty(Mono.defer(() -> {
                            log.warn("{}월 데이터 없음. 스킵", month);
                            return Mono.empty(); // emit 없이 skip
                        }))
                        .flatMap(response -> {
                            int totalCount = response.getBody().getTotalCount();
                            int numOfRows = response.getBody().getNumOfRows();
                            int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

                            return runAptJob(regionCode, month, adres, jobId, totalPages);
                        });
            })
            .then() // 전체 month 작업 완료 시점
            .subscribe(
                    null,
                    error -> {
                        batchJobStatusService.saveStatus(request.getJobId(), "FAILED");
                        log.error("{}지역 아파트거래 작업 실패: {}", adres, error.getMessage(), error);
                    },
                    () -> {
                        batchJobStatusService.saveStatus(request.getJobId(), "DONE");
                        log.info("{}지역 아파트거래 작업 완료", adres);
                    }
            );
        /*for (String month : monthList) {
            Integer curPage = MONTH_PAGING.get(month);
            Integer totalPages = TOTAL_PAGES.getOrDefault(month, -1);
            if (totalPages == -1) {
                XmlSuccessResponse response = webClientService.executeXmlRequest(
                        buildUrl(1, regionCode, month),
                        BodyInserters.empty(),
                        apiResponseUtil::parseResponse
                ).blockFirst();

                if (response == null) {
                    // 데이터가 없으면 이 month는 스킵하고 다음 month로
                    MONTH_PAGING.put(month, -1);
                    continue;
                }

                int totalCount = response.getBody().getTotalCount();
                int numOfRows = response.getBody().getNumOfRows();
                totalPages = (int) Math.ceil((double) totalCount / numOfRows);

                TOTAL_PAGES.put(month, totalPages);
            }

            // 현재 month의 페이지가 totalPages를 넘으면 다음 month로
            if (curPage >= totalPages) {
                continue;
            }
            runAptJob(regionCode, month, adres, jobId);
            MONTH_PAGING.put(month, curPage + 1);
        }*/
    }
    public Mono<Void> runAptJob(String regionCode, String month, String adres, Long jobId, int totalPages) {
        return Flux.range(1, totalPages)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(pageNo -> {
                    URI url = buildUrl(pageNo, regionCode, month);
                    return webClientService.executeXmlRequest(url, BodyInserters.empty(), apiResponseUtil::parseResponse)
                            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)));
                })
                .flatMap(apiResponse -> {
                    return Flux.fromIterable(Optional.ofNullable(apiResponse.getBody())
                                    .map(ResponseBody::getItems)
                                    .orElse(Collections.emptyList()))
                            .buffer(100)
                            .flatMap(this::saveBatchToDatabase);
                })
                .then(); // 작업 완료 후 signal만 반환
    }

    public void runAptJob(String regionCode, String month, String adres, Long jobId) {
        generatePagedUris(regionCode, month)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(url -> webClientService.executeXmlRequest(url, BodyInserters.empty(), apiResponseUtil::parseResponse)
                        .retryWhen(
                                Retry.fixedDelay(3, Duration.ofSeconds(2))
                        ))
                .flatMap(apiResponse -> {
                    return Flux.fromIterable(Optional.ofNullable(apiResponse.getBody())
                                    .map(ResponseBody::getItems)
                                    .orElse(Collections.emptyList()))
                            .buffer(100)
                            .flatMap(this::saveBatchToDatabase);

                })
                .then()
                .subscribe(
                        null,
                        error -> {
                            batchJobStatusService.saveStatus(jobId, "FAILED");
                            log.error("{}지역 아파트 정보 작업 실패: {}",adres, error.getMessage(), error);
                        },
                        () -> {
                            batchJobStatusService.saveStatus(jobId, "DONE");
                            log.info("{}지역 아파트 정보 작업 완료",adres);
                        }
                );
    }
    public Flux<URI> generatePagedUris(String regionCode, String month) {
        return webClientService.executeXmlRequest(
                        buildUrl(1, regionCode, month),
                        BodyInserters.empty(),
                        apiResponseUtil::parseResponse
                )
                .flatMap(response -> {
                    int totalCount = response.getBody().getTotalCount();
                    int numOfRows = response.getBody().getNumOfRows();
                    int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

                    log.info("총 페이지 수: {}", totalPages);

                    return Flux.range(0, totalPages)
                            .map(page -> buildUrl(page, regionCode, month));
                });
    }

    private Mono<Void> saveBatchToDatabase(List<ApartmentItem> items) {
        List<AptTrade> entities = aptTradeMapper.toEntities(items);
        return aptTradeRepository.saveAll(entities)
                .then();
    }

    private URI buildUrl(int pageNo, String regionCode, String dealDate) {
        String baseUrl = "https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade";
        String serviceKey = apiProperties.getKey();
        int numOfRows = 20;
        URI uri = URI.create(baseUrl
                + "?LAWD_CD="+regionCode
                + "&DEAL_YMD="+dealDate
                + "&serviceKey="+serviceKey
                + "&pageNo=" +pageNo
                + "&numOfRows="+numOfRows);
        return uri;
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
