package io.insight.real.city.batch.job.config;

import io.insight.real.city.batch.emp.EmploymentItemReader;
import io.insight.real.city.batch.emp.EmploymentItemWriter;
import io.insight.real.city.dto.request.CityBasicInfoRequest;
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
public class CityEmpBatchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EmploymentItemReader employmentItemReader;
    private final EmploymentItemWriter employmentItemWriter;

    @Bean(name = "updateCityEmpJob")
    public Job updateCityBasicInfoJob(
            @Qualifier("updateCityEmpStep") Step updateCityEmpStep) {
        return new JobBuilder("updateCityEmpJob", jobRepository)
                .start(updateCityEmpStep)
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .build();
    }

    @Bean(name="updateCityEmpStep")
    public Step updateCityEmpStep() {
        return new StepBuilder("updateCityEmpStep", jobRepository)
                .<List<CityBasicInfoRequest>, List<CityBasicInfoRequest>>chunk(100, transactionManager)
                .reader(employmentItemReader)
                .writer(employmentItemWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(1)
                .build();
    }
}
