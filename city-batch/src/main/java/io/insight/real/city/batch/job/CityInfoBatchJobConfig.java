package io.insight.real.city.batch.job;

import io.insight.real.city.batch.city.CityBasicItemReader;
import io.insight.real.city.batch.city.CityBasicItemWriter;
import io.insight.real.city.dto.request.CityBasicInfoRequest;
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

@Configuration
public class CityInfoBatchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CityBasicItemReader cityBasicItemReader;
    private final CityBasicItemWriter cityBasicItemWriter;
    private final Step cityRankingStep;

    public CityInfoBatchJobConfig(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        CityBasicItemReader cityBasicItemReader,
        CityBasicItemWriter cityBasicItemWriter,
        @Qualifier("cityRankingStep") Step cityRankingStep
    ){
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.cityBasicItemReader = cityBasicItemReader;
        this.cityBasicItemWriter = cityBasicItemWriter;
        this.cityRankingStep = cityRankingStep;
    }

    @Bean(name = "updateCityBasicInfoJob")
    public Job updateCityBasicInfoJob(
            @Qualifier("updateCityBasicInfoStep") Step updateCityBasicInfoStep) {
        return new JobBuilder("updateCityBasicInfoJob", jobRepository)
                .start(updateCityBasicInfoStep)
                .on("COMPLETED").to(cityRankingStep)
                .end()
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .build();
    }

    @Bean(name="updateCityBasicInfoStep")
    public Step updateCityBasicInfoStep() {
        return new StepBuilder("updateCityBasicInfoStep", jobRepository)
                .<List<CityBasicInfoRequest>, List<CityBasicInfoRequest>>chunk(100, transactionManager)
                .reader(cityBasicItemReader)
                .writer(cityBasicItemWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(1)
                .build();
    }



}
