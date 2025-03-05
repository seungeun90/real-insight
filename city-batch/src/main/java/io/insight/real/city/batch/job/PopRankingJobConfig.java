package io.insight.real.city.batch.job;

import io.insight.real.city.batch.rank.PopRankingProcessor;
import io.insight.real.city.batch.rank.PopRankingReader;
import io.insight.real.city.batch.rank.PopRankingWriter;
import io.insight.real.city.dto.PopRankingDto;
import io.insight.real.city.repository.dao.PopulationDao;
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
public class PopRankingJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final PopRankingReader popRankingReader;
    private final PopRankingProcessor popRankingProcessor;
    private final PopRankingWriter popRankingWriter;

    @Bean(name = "popRankingJob")
    public Job updatePopRankingJob(
            @Qualifier("popRankingStep") Step popRankingStep) {
        return new JobBuilder("popRankingJob", jobRepository)
                .start(popRankingStep)
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                .build();
    }

    @Bean(name="popRankingStep")
    public Step popRankingStep() {
        return new StepBuilder("popRankingStep", jobRepository)
                .<List<PopulationDao>, List<PopRankingDto>>chunk(100, transactionManager)
                .reader(popRankingReader)
                .processor(popRankingProcessor)
                .writer(popRankingWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(1)
                .build();
    }
}
