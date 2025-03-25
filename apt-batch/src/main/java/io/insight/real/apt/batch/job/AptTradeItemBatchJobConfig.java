package io.insight.real.apt.batch.job;

import io.insight.real.apt.batch.AptTradeItemReader;
import io.insight.real.apt.batch.AptTradeItemWriter;
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

@Configuration
public class AptTradeItemBatchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final AptTradeItemReader aptTradeItemReader;
    private final AptTradeItemWriter aptTradeItemWriter;
   // private final BatchJobStatusListener batchJobStatusListener;

    public AptTradeItemBatchJobConfig(
        JobRepository jobRepository,
        @Qualifier("platformTransactionManager") PlatformTransactionManager transactionManager,
        AptTradeItemReader aptTradeItemReader,
        AptTradeItemWriter aptTradeItemWriter
        //BatchJobStatusListener batchJobStatusListener
    ){
        this.jobRepository = jobRepository;
        this.platformTransactionManager = transactionManager;
        this.aptTradeItemReader = aptTradeItemReader;
        this.aptTradeItemWriter = aptTradeItemWriter;
        //this.batchJobStatusListener = batchJobStatusListener;
    }

    @Bean(name = "updateAptTradeJob")
    public Job updateCityBasicInfoJob(
            @Qualifier("updateAptTradeStep") Step updateAptInfoStep) {
        return new JobBuilder("updateAptTradeJob", jobRepository)
                .start(updateAptInfoStep)
          //      .listener(batchJobStatusListener)
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .build();
    }

    @Bean(name="updateAptTradeStep")
    public Step updateAptTradeStep() {
        return new StepBuilder("updateAptTradeStep", jobRepository)
                .<URI, URI>chunk(10, platformTransactionManager)
                .reader(aptTradeItemReader)
                .writer(aptTradeItemWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(1)
                .build();
    }



}
