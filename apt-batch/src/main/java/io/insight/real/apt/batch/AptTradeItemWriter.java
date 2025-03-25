package io.insight.real.apt.batch;

import io.insight.real.apt.batch.job.BatchJobStatusService;
import io.insight.real.apt.dto.response.ApartmentItem;
import io.insight.real.apt.dto.response.ResponseBody;
import io.insight.real.apt.repository.jpa.BatchJobRepository;
import io.insight.real.apt.repository.r2dbc.entity.AptTrade;
import io.insight.real.apt.repository.mapper.AptTradeMapper;
import io.insight.real.apt.repository.r2dbc.AptTradeRepository;
import io.insight.real.apt.service.CommonWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AptTradeItemWriter implements ItemWriter<URI> {
    private final CommonWebClientService webClientService;
    private final ApiResponseUtil apiResponseUtil;
    private final AptTradeRepository aptTradeRepository;
    private final AptTradeMapper aptTradeMapper;
    private final BatchJobStatusService batchJobStatusService;
    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void write(Chunk<? extends URI> chunk) throws Exception {
        List<URI> uris = chunk.getItems().stream().map(uri->((URI)uri)).toList();
        if (uris.isEmpty()) return;
        Flux.fromIterable(uris)
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
                .subscribe(
                        success -> {
                            JobParameters params = stepExecution.getJobParameters();
                            Long jobId = params.getLong("jobId");
                            log.info("Job id is {}", jobId);
                            if(jobId != null) {
                                batchJobStatusService.saveStatus(jobId, "DONE");
                            }
                            log.info("데이터 저장 완료");
                        },
                        error -> {
                            JobParameters params = stepExecution.getJobParameters();
                            Long jobId = params.getLong("jobId");
                            log.info("Job id is {}", jobId);
                            if(jobId != null) {
                                batchJobStatusService.saveStatus(jobId, "FAILED");
                            }
                            log.error(" 데이터 저장 중 오류 발생", error);
                        }
                );
    }


    private Mono<Integer> saveBatchToDatabase(List<ApartmentItem> items) {
        List<AptTrade> entities = aptTradeMapper.toEntities(items);
        return aptTradeRepository.saveAll(entities)
                .then()
                .thenReturn(items.size());
    }

}
