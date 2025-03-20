package io.insight.real.apt.batch;

import io.insight.real.apt.dto.response.ApartmentItem;
import io.insight.real.apt.repository.r2dbc.entity.AptTrade;
import io.insight.real.apt.repository.mapper.AptTradeMapper;
import io.insight.real.apt.repository.r2dbc.AptTradeRepository;
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
public class AptTradeItemWriter implements ItemWriter<URI> {
    private final CommonWebClientService webClientService;
    private final ApiResponseUtil apiResponseUtil;
    private final AptTradeRepository aptTradeRepository;
    private final AptTradeMapper aptTradeMapper;

    @Override
    public void write(Chunk<? extends URI> chunk) throws Exception {
        List<URI> uris = chunk.getItems().stream().map(uri->((URI)uri)).toList();
        if (uris.isEmpty()) return;
        Flux.fromIterable(uris)
                .delayElements(Duration.ofMillis(500))
                .flatMap(url -> webClientService.executeXmlRequest(url, BodyInserters.empty(), apiResponseUtil::parseResponse))
                .flatMap(apiResponse -> {
                    return Flux.fromIterable(apiResponse.getBody().getItems())
                            .buffer(100)
                            .flatMap(this::saveBatchToDatabase);
                })
                .subscribe(
                        success -> log.info("데이터 저장 완료"),
                        error -> log.error(" 데이터 저장 중 오류 발생", error)
                );
    }

    private Mono<Void> saveBatchToDatabase(List<ApartmentItem> items) {
        List<AptTrade> entities = aptTradeMapper.toEntities(items);
        return aptTradeRepository.saveAll(entities)
                .then();
    }


}
