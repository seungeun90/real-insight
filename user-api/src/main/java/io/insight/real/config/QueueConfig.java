package io.insight.real.config;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.CityPopulation;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.MessageQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Bean(name="populationDataQueue")
    public MessageQueue<CityPopulation> populationDataQueue() {
        return new MessageQueue<>();
    }

    @Bean(name="cityDataQueue")
    public MessageQueue<CityBasicInfo> cityDataQueue() {
        return new MessageQueue<>();
    }
    @Bean(name="rankingDataQueue")
    public MessageQueue<CityRankingData> rankingDataQueue() {
        return new MessageQueue<>();
    }

}

