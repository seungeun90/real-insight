package io.insight.real.apt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://sgisapi.kostat.go.kr/OpenAPI3")
                .build();
    }
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl("https://sgisapi.kostat.go.kr/OpenAPI3").build();
    }
}