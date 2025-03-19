package io.insight.real.city.service;

import io.insight.real.city.config.properties.QueueProperties;
import io.insight.real.city.dto.PopRankingDto;
import io.insight.real.city.dto.RankedInfoDto;
import io.insight.real.city.repository.entity.CityBasicInfo;
import io.insight.real.city.repository.entity.CityPopulation;
import io.insight.real.city.repository.entity.Employment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageSenderService {
    private final AmqpTemplate rabbitTemplate;
    private final QueueProperties queueProperties;

    public void publishCityMessage(List<CityBasicInfo> cities){
        QueueProperties.City cityProperty = queueProperties.getCity();
        publishMessage(cities, cityProperty.getExchangeName(), cityProperty.getRoutingKey());
    }

    public void publishEmpMessage(List<Employment> employments){
        QueueProperties.Employment employment = queueProperties.getEmployment();
        publishMessage(employments, employment.getExchangeName(), employment.getRoutingKey());
    }

    public void publishPopMessage(List<CityPopulation> populations){
        QueueProperties.Population population = queueProperties.getPopulation();
        publishMessage(populations, population.getExchangeName(), population.getRoutingKey());
    }

    public void publishCityRankMessage(List<RankedInfoDto> rankings){
        QueueProperties.Ranking ranking = queueProperties.getRanking();
        publishMessage(rankings, ranking.getExchangeName(), ranking.getCity().getRoutingKey());
    }

    public void publishPopRankMessage(List<PopRankingDto> rankings){
        QueueProperties.Ranking ranking = queueProperties.getRanking();
        publishMessage(rankings, ranking.getExchangeName(), ranking.getPopulation().getRoutingKey());
    }
    public void publishDistrictMessage(Map<String, Object> map){
        QueueProperties.District district = queueProperties.getDistrict();
        publishMessage(map, district.getExchangeName(), district.getRoutingKey());
    }
    public void publishMessage(Object message, String exchange, String routingKey) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
    public void publishMessage(List<?> messages, String exchange, String routingKey) {
        messages.forEach(message -> rabbitTemplate.convertAndSend(exchange, routingKey, message));
    }
}
