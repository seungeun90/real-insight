package io.insight.real.city.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://sgisapi.kostat.go.kr/OpenAPI3")
                .build();
    }
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .wiretap(true) // 네트워크 로그 확인용 (선택 사항)
                .headers(headers -> headers.add("Content-Type", "application/x-www-form-urlencoded"))
                .responseTimeout(Duration.ofSeconds(30));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient)) // ✅ 커스텀 HttpClient 적용
                .baseUrl("https://sgisapi.kostat.go.kr/OpenAPI3")
                .defaultHeader("Content-Type", "application/x-www-form-urlencoded") // ✅ 헤더 설정
                .build();

    }
}