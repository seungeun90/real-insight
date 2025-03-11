package io.insight.real.apt.batch;

import io.insight.real.apt.dto.response.AptIdInfo;
import io.insight.real.apt.dto.response.AptResponse;
import io.insight.real.apt.repository.entity.AptInfo;
import io.insight.real.apt.repository.mapper.AptInfoMapper;
import io.insight.real.apt.repository.r2dbc.AptInfoCustomRepository;
import io.insight.real.apt.repository.r2dbc.AptInfoRepository;
import io.insight.real.apt.service.CommonWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final AptInfoRepository aptInfoRepository;
    private final AptInfoCustomRepository aptInfoCustomRepository;

    @Override
    public void write(Chunk<? extends URI> chunk) throws Exception {
        List<URI> uris = chunk.getItems().stream().map(uri->((URI)uri)).toList();
        if (uris.isEmpty()) return;
        Flux.fromIterable(uris)
               // .doOnNext(uri -> log.info("📌 요청되는 URI: {}", uri))
                //.distinct()
                .delayElements(Duration.ofMillis(500))
                .flatMap(url -> webClientService.executeRequest(url, AptResponse.class, BodyInserters.empty(), Flux::just))
                .flatMap(response -> {
                    Integer page = response.getPage();
                    //   log.info("✅ matchCount {}, totalPages {}",response.getPage(), response.getTotalCount());
                    return Flux.fromIterable(response.getData())
                                    .filter(dto -> "1".equals(dto.getComplexGbCd()))
                            .map(dto->{
                                return dto;
                            })
                                    .buffer(100) //
                                    .flatMap(this::saveBatchToDatabase);
                        })
                .subscribe(
                        success -> log.info("데이터 저장 완료"),
                        error -> log.error(" 데이터 저장 중 오류 발생", error)
                );
    }

    private Mono<Void> saveBatchToDatabase(List<AptIdInfo> items) {
        if (items.isEmpty()) {
            log.warn("⚠ 저장할 데이터가 없음!");
            return Mono.empty();
        }
        List<AptInfo> entities = aptInfoMapper.toEntities(items);
        return aptInfoCustomRepository.bulkUpsert(entities)
             //   .doOnNext(saved -> log.info("저장 완료: {}", saved))
               // .doOnError(error -> log.error("DB 저장 오류: {}", error.getMessage(), error))
                .then();
    }
}
