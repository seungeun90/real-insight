package io.insight.real.apt.config.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

//@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
public class BatchConfig {

    private final DataSource batchDataSource;
    private final DataSourceTransactionManager batchTransactionManager;

    public BatchConfig(@Qualifier("batchDataSource") DataSource batchDataSource,
                       @Qualifier("batchTransactionManager") DataSourceTransactionManager batchTransactionManager){
        this.batchDataSource = batchDataSource;
        this.batchTransactionManager = batchTransactionManager;
    }
    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(batchDataSource);
        factory.setTransactionManager(batchTransactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name="platformTransactionManager")
    public PlatformTransactionManager platformTransactionManager() {
        return batchTransactionManager;
    }
    @Bean(name = "asyncJobLauncher")
    @Primary
    public JobLauncher asyncJobLauncher(JobRepository jobRepository, TaskExecutor batchTaskExecutor) {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(batchTaskExecutor); // 비동기 실행 설정
        return jobLauncher;
    }

    @Bean
    public TaskExecutor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("batch-thread-");
        executor.initialize();
        return executor;
    }



}
