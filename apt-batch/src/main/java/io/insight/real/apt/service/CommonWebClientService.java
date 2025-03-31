package io.insight.real.apt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommonWebClientService {
    private final WebClient webClient;

    public <R> Flux<R> executeXmlRequest(URI url,
                                            BodyInserter<?, ? super ReactiveHttpOutputMessage> bodyInserter,
                                            Function<String, Flux<R>> function) {
        return webClient
                .method(HttpMethod.GET)
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(bodyInserter)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                //  .retry(1)
                .onErrorResume(error -> {
                    log.error("API 호출 중 오류 발생", error);
                    log.error("{}", error.getMessage());
                    return Mono.empty(); // 에러 발생 시 Flux가 멈추지 않고 계속 진행
                })
                .flatMapMany(function);
    }

    public  <T,R> Flux<R> executeRequest(URI url,
                                         Class<T> responseType,
                                         BodyInserter<?, ? super ClientHttpRequest> bodyInserter,
                                         Function<? super T, ? extends Publisher<? extends R>> function) {
        return webClient
                .method(HttpMethod.GET)
                .uri(url)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(bodyInserter)
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(60))
                //  .retry(1)
                .onErrorResume(error -> {
                    log.error("API 호출 중 오류 발생", error);
                    return Mono.empty(); // 에러 발생 시 Flux가 멈추지 않고 계속 진행
                })
                .flatMapMany(function);
    }

}
