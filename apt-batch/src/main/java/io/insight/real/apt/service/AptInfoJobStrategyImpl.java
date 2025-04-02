package io.insight.real.apt.service;

import io.insight.real.apt.config.ApiProperties;
import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.JobName;
import io.insight.real.apt.dto.JobStatus;
import io.insight.real.apt.dto.in.AptIdInfo;
import io.insight.real.apt.dto.in.AptResponse;
import io.insight.real.apt.repository.mapper.AptInfoMapper;
import io.insight.real.apt.repository.r2dbc.AptInfoCustomRepository;
import io.insight.real.apt.repository.r2dbc.entity.AptInfo;
import io.insight.real.apt.service.in.JobStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AptInfoJobStrategyImpl implements JobStrategy {
    private final CommonWebClientService webClientService;
    private final ApiProperties apiProperties;
    private final AptInfoCustomRepository aptInfoCustomRepository;
    private final BatchJobStatusService batchJobStatusService;
    private final AptInfoMapper aptInfoMapper;

    @Override
    public void triggerJob(BatchJobRequest request){
        runAptJob(request.getAdres(), request.getJobId());
    }

    @Override
    public JobName getJobType() {
        return JobName.APT_INFO_JOB;
    }

    private void runAptJob(String adres, Long jobId) {
        generatePagedUris(adres)
                .delayElements(Duration.ofMillis(500)) // 과도한 요청 방지
                .flatMap(uri -> webClientService.executeRequest(uri, AptResponse.class, BodyInserters.empty(), Flux::just))
                .flatMap(response -> Flux.fromIterable(response.getData())
                        .filter(dto -> "1".equals(dto.getComplexGbCd()))
                        .buffer(100)
                        .flatMap(this::saveBatchToDatabase)
                )
                .then()
                .subscribe(
                        null,
                        error -> {
                            batchJobStatusService.saveStatus(jobId, JobStatus.FAILED.name());
                            log.error("{}지역 아파트 정보 작업 실패: {}",adres, error.getMessage(), error);
                        },
                        () -> {
                            batchJobStatusService.saveStatus(jobId, JobStatus.DONE.name());
                            log.info("{}지역 아파트 정보 작업 완료",adres);
                        }
                );
    }
    private Flux<URI> generatePagedUris(String adres) {
        return webClientService.executeRequest(buildUrl(0, adres), AptResponse.class, BodyInserters.empty(), Mono::just)
                .flatMap(firstResponse -> {
                    int matchCount = firstResponse.getMatchCount();
                    int perPage = firstResponse.getPerPage();
                    int totalPages = (int) Math.ceil((double) matchCount / perPage);

                    log.info("총 페이지 수: {}", totalPages);

                    return Flux.range(0, totalPages)
                            .map(page -> buildUrl(page, adres));
                });
    }

    private Mono<Void> saveBatchToDatabase(List<AptIdInfo> items) {
        if (items.isEmpty()) {
            log.warn("저장할 데이터가 없음!");
            return Mono.empty();
        }
        List<AptInfo> entities = aptInfoMapper.toEntities(items);
        return aptInfoCustomRepository.bulkUpsert(entities)
                .then();
    }

    private URI buildUrl(int pageNo, String addr) {
        String baseUrl = apiProperties.getUrl();
        String serviceKey = apiProperties.getKey();
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
