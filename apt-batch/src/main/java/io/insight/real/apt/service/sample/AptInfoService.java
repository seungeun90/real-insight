package io.insight.real.apt.service.sample;

import io.insight.real.apt.config.ApiProperties;
import io.insight.real.apt.dto.response.AptIdInfo;
import io.insight.real.apt.dto.response.AptResponse;
import io.insight.real.apt.repository.r2dbc.entity.AptInfo;
import io.insight.real.apt.repository.mapper.AptInfoMapper;
import io.insight.real.apt.repository.r2dbc.AptInfoRepository;
import io.insight.real.apt.service.CommonWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AptInfoService {
    private final CommonWebClientService webClientService;
    private final AptInfoMapper aptInfoMapper;
    private final AptInfoRepository aptInfoRepository;
    private final ApiProperties apiProperties;

    public void updateAptTradeInfo(String addr){
        URI url = buildUrl(0, addr);
        fetchAllPages(url).doOnTerminate(
                (() -> System.out.println("모든 요청과 DB 저장 완료"))
        ).subscribe();
    }

    public Flux<Void> fetchAllPages(URI url) {
        return webClientService.executeRequest(url, AptResponse.class, BodyInserters.empty(), Flux::just)
                .flatMap(apiResponse -> {
                    List<AptIdInfo> list = apiResponse.getData();
                    Integer perPage = apiResponse.getPerPage();
                    int matchCount = apiResponse.getMatchCount();
                    int totalPages = (int) Math.ceil((double) matchCount / perPage);

                    log.info("matchCount {}, totalPages {}",matchCount, totalPages);
                    //아파트만 저장
                    Flux<AptIdInfo> firstPageItems =
                            Flux.fromIterable(list)
                                    .filter(dto-> dto.getComplexGbCd().equals("1"));

                    Flux<AptIdInfo> otherPagesItems = Flux.range(2, totalPages)
                           // .log()
                            .delayElements(Duration.ofMillis(500))
                            .flatMap(pageNo -> webClientService.executeRequest(buildUrl(pageNo, url),AptResponse.class, BodyInserters.empty(), Flux::just))
                            .flatMap(response -> {
                                log.info("페이지 {}", response.getPage());
                                return Flux.fromIterable(response.getData())
                                        .filter(dto -> "1".equals(dto.getComplexGbCd()));
                            });
                    return firstPageItems.mergeWith(otherPagesItems);
                })
                .buffer(100)
                .flatMap(this::saveBatchToDatabase);

    }
    private Mono<Void> saveBatchToDatabase(List<AptIdInfo> items) {
        log.info("db save target=== {}", items.size());
        if (items.isEmpty()) {
            log.warn("저장할 데이터가 없음!");
            return Mono.empty();
        }
        List<AptInfo> entities = aptInfoMapper.toEntities(items);
        return aptInfoRepository.saveAll(entities)
                .doOnNext(saved -> log.info("저장 완료: {}", saved))
                .doOnError(error -> log.error("DB 저장 오류: {}", error.getMessage(), error))
                .then();
    }

    private URI buildUrl(int pageNo, String addr) {
        String baseUrl = apiProperties.getUrl();
        String serviceKey = apiProperties.getKey();
        int numOfRows = 100;
        String encodedCond = URLEncoder.encode("cond[ADRES::LIKE]", StandardCharsets.UTF_8);
        String encodedAddr = URLEncoder.encode(addr, StandardCharsets.UTF_8).replace("+", "%20"); // 띄어쓰기`+`를 `%20`으로 변환

        URI uri = URI.create(baseUrl
                + "?perPage=" + numOfRows
                + "&returnType=JSON"
                + "&" + encodedCond + "=" + encodedAddr
                + "&serviceKey=" + serviceKey
                + "&page=" + pageNo);

        return uri;

    }

    private URI buildUrl(int pageNo, URI originalUrl){
        String url = null;
        try {
            url = originalUrl.toURL().toString();
            return new URI(url.replaceAll("page=\\d+", "page=" + pageNo));
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
