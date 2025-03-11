package io.insight.real.apt.batch.job;

import io.insight.real.apt.batch.AptItemReader;
import io.insight.real.apt.batch.AptItemWriter;
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

import java.net.URI;
import java.util.List;

@Configuration
public class AptItemBatchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final AptItemReader aptItemReader;
    private final AptItemWriter aptItemWriter;

    public AptItemBatchJobConfig(
        JobRepository jobRepository,
        @Qualifier("platformTransactionManager") PlatformTransactionManager transactionManager,
        AptItemReader aptItemReader,
        AptItemWriter aptItemWriter
    ){
        this.jobRepository = jobRepository;
        this.platformTransactionManager = transactionManager;
        this.aptItemReader = aptItemReader;
        this.aptItemWriter = aptItemWriter;
    }

    @Bean(name = "updateAptInfoJob")
    public Job updateCityBasicInfoJob(
            @Qualifier("updateAptInfoStep") Step updateAptInfoStep) {
        return new JobBuilder("updateAptInfoJob", jobRepository)
                .start(updateAptInfoStep)
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .build();
    }

    @Bean(name="updateAptInfoStep")
    public Step updateAptInfoStep() {
        return new StepBuilder("updateAptInfoStep", jobRepository)
                .<URI, URI>chunk(10, platformTransactionManager)
                .reader(aptItemReader)
                .writer(aptItemWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(1)
                .build();
    }



}
