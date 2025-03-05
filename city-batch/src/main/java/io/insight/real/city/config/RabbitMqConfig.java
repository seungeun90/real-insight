package io.insight.real.city.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.insight.real.city.config.properties.QueueProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class RabbitMqConfig {
    private final QueueProperties queueProperties;
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // JSON → DTO 변환 시, snake_case를 camelCase로 자동 변환
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        // JSON 직렬화(보낼 때)는 기본 필드명(camelCase) 유지
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public List<Queue> queueList() {
        QueueProperties.Population population = queueProperties.getPopulation();
        QueueProperties.City city = queueProperties.getCity();
        QueueProperties.DeadLetter deadLetter = queueProperties.getDeadLetter();
        QueueProperties.Ranking ranking = queueProperties.getRanking();
        QueueProperties.Employment emp = queueProperties.getEmployment();
        QueueProperties.District district = queueProperties.getDistrict();
        Map<String, Object> deadLetterConfig = new HashMap<>(){{
            put(deadLetter.getExchangeHeaderName(), deadLetter.getExchangeName());
            put(deadLetter.getRoutingHeaderName(), deadLetter.getRoutingKey());
        }};
        return List.of(
                new Queue(population.getQueueName(),
                        population.isDurable(),
                        population.isExclusive(),
                        population.isAutoDelete(),
                        deadLetterConfig),
                new Queue(district.getQueueName(),
                        district.isDurable(),
                        district.isExclusive(),
                        district.isAutoDelete()),
                new Queue(city.getQueueName(),
                        city.isDurable(),
                        city.isExclusive(),
                        city.isAutoDelete(),
                        deadLetterConfig),
                new Queue(ranking.getCity().getQueueName(),
                        ranking.isDurable(),
                        ranking.isExclusive(),
                        ranking.isAutoDelete(),
                        deadLetterConfig),
                new Queue(ranking.getPopulation().getQueueName(),
                        ranking.isDurable(),
                        ranking.isExclusive(),
                        ranking.isAutoDelete(),
                        deadLetterConfig),
                new Queue(emp.getQueueName(),
                        emp.isDurable(),
                        emp.isExclusive(),
                        emp.isAutoDelete(),
                        deadLetterConfig),
                new Queue(deadLetter.getQueueName(), deadLetter.isDurable())
        );
    }


    @Bean
    public List<DirectExchange> exchangeList() {
        return List.of(
                new DirectExchange(queueProperties.getPopulation().getExchangeName()),
                new DirectExchange(queueProperties.getDeadLetter().getExchangeName())
        );
    }

    @Bean
    public List<Binding> bindingList() {
        QueueProperties.Population population = queueProperties.getPopulation();
        QueueProperties.City city = queueProperties.getCity();
        QueueProperties.DeadLetter deadLetter = queueProperties.getDeadLetter();
        QueueProperties.Ranking ranking = queueProperties.getRanking();
        QueueProperties.Employment employment = queueProperties.getEmployment();
        QueueProperties.District district = queueProperties.getDistrict();
        return List.of(
                BindingBuilder.bind(new Queue(district.getQueueName()))
                        .to(new DirectExchange(district.getExchangeName()))
                        .with(district.getRoutingKey()),

                // Population 큐 바인딩
                BindingBuilder.bind(new Queue(population.getQueueName()))
                        .to(new DirectExchange(population.getExchangeName()))
                        .with(population.getRoutingKey()),

                // City 큐 바인딩
                BindingBuilder.bind(new Queue(city.getQueueName()))
                        .to(new DirectExchange(population.getExchangeName()))
                        .with(city.getRoutingKey()),
                // Employment
                BindingBuilder.bind(new Queue(employment.getQueueName()))
                        .to(new DirectExchange(population.getExchangeName()))
                        .with(employment.getRoutingKey()),

                // Ranking 큐 바인딩
                BindingBuilder.bind(new Queue(ranking.getCity().getQueueName()))
                        .to(new DirectExchange(population.getExchangeName()))
                        .with(ranking.getCity().getRoutingKey()),

                BindingBuilder.bind(new Queue(ranking.getPopulation().getQueueName()))
                        .to(new DirectExchange(population.getExchangeName()))
                        .with(ranking.getPopulation().getRoutingKey()),

                // Dead Letter Queue를 Dead Letter Exchange에 바인딩
                BindingBuilder.bind(new Queue(deadLetter.getQueueName()))
                        .to(new DirectExchange(deadLetter.getExchangeName()))
                        .with(deadLetter.getRoutingKey())
        );
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer(ObjectMapper objectMapper) {
        return rabbitTemplate -> rabbitTemplate.setMessageConverter(jsonMessageConverter(objectMapper));
    }
}
