package io.insight.real.apt.batch;

import io.insight.real.apt.batch.job.BatchJobStatusService;
import io.insight.real.apt.dto.response.AptIdInfo;
import io.insight.real.apt.dto.response.AptResponse;
import io.insight.real.apt.repository.jpa.BatchJobRepository;
import io.insight.real.apt.repository.r2dbc.entity.AptInfo;
import io.insight.real.apt.repository.mapper.AptInfoMapper;
import io.insight.real.apt.repository.r2dbc.AptInfoCustomRepository;
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

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AptItemWriter implements ItemWriter<URI> {
    private final CommonWebClientService webClientService;
    private final AptInfoMapper aptInfoMapper;
    private final AptInfoCustomRepository aptInfoCustomRepository;
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
                .delayElements(Duration.ofMillis(500))
                .flatMap(url -> webClientService.executeRequest(url, AptResponse.class, BodyInserters.empty(), Flux::just))
                .flatMap(response -> {
                    return Flux.fromIterable(response.getData())
                            .filter(dto -> "1".equals(dto.getComplexGbCd()))
                            .buffer(100)
                            .flatMap(this::saveBatchToDatabase);
                })
                .subscribe(
                        null,
                        error -> {
                            JobParameters params = stepExecution.getJobParameters();
                            Long jobId = params.getLong("jobId");
                            if(jobId != null) {
                                batchJobStatusService.saveStatus(jobId, "FAILED");
                                log.error(" 데이터 저장 중 오류 발생", error);
                            }
                        },
                        () -> {
                            JobParameters params = stepExecution.getJobParameters();
                            Long jobId = params.getLong("jobId");
                            if(jobId != null) {
                                batchJobStatusService.saveStatus(jobId, "DONE");
                                log.info("데이터 저장 완료");
                            }

                        }
                );
    }


    private Mono<Integer> saveBatchToDatabase(List<AptIdInfo> items) {
        if (items.isEmpty()) {
            log.warn("저장할 데이터가 없음!");
            return Mono.empty();
        }
        List<AptInfo> entities = aptInfoMapper.toEntities(items);
        return aptInfoCustomRepository.bulkUpsert(entities)
                .thenReturn(items.size());
    }
}
