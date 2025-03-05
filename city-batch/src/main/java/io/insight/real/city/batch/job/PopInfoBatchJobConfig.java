package io.insight.real.city.batch.job;

import io.insight.real.city.batch.pop.PopulationItemReader;
import io.insight.real.city.batch.pop.PopulationItemWriter;
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
public class PopInfoBatchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PopulationItemReader populationItemReader;
    private final PopulationItemWriter populationItemWriter;
    private final Step popRankingStep;

    public PopInfoBatchJobConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PopulationItemReader populationItemReader,
            PopulationItemWriter populationItemWriter,
            @Qualifier("popRankingStep") Step popRankingStep
    ){
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.populationItemReader = populationItemReader;
        this.populationItemWriter = populationItemWriter;
        this.popRankingStep = popRankingStep;
    }


    @Bean(name="updatePopulationInfoJob")
    public Job updatePopulationInfoJob(
            @Qualifier("updatePopulationInfoStep") Step updatePopulationInfoStep) {
        return new JobBuilder("updatePopulationInfoJob", jobRepository)
                .start(updatePopulationInfoStep)
                .on("COMPLETED").to(popRankingStep)
                .end()
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .build();
    }

    @Bean(name="updatePopulationInfoStep")
    public Step updatePopulationInfoStep() {
        return new StepBuilder("updatePopulationInfoStep", jobRepository)
                .<List<CityBasicInfoRequest>, List<CityBasicInfoRequest>>chunk(100, transactionManager)
                .reader(populationItemReader)
                .writer(populationItemWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(1)
                .build();
    }



}
