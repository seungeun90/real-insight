package io.insight.real.city.batch.job.config;

import io.insight.real.city.batch.rank.CityInfoRankingProcessor;
import io.insight.real.city.batch.rank.CityInfoRankingReader;
import io.insight.real.city.batch.rank.CityInfoRankingWriter;
import io.insight.real.city.dto.CityBasicDto;
import io.insight.real.city.dto.RankedInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class CityRankingJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final CityInfoRankingReader cityInfoRankingReader;
    private final CityInfoRankingWriter cityInfoRankingWriter;
    private final CityInfoRankingProcessor cityInfoRankingProcessor;

    @Bean(name = "cityRankingJob")
    public Job updateCityRankingJob(
            @Qualifier("cityRankingStep") Step cityRankingStep) {
        return new JobBuilder("cityRankingJob", jobRepository)
                .start(cityRankingStep)
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .build();
    }

    @Bean(name="cityRankingStep")
    public Step cityRankingStep() {
        return new StepBuilder("cityRankingStep", jobRepository)
                .<List<CityBasicDto>, List<RankedInfoDto>>chunk(100, transactionManager)
                .reader(cityInfoRankingReader)
                .processor(cityInfoRankingProcessor)
                .writer(cityInfoRankingWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(1)
                .build();
    }
}
