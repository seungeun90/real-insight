package io.insight.real.city.batch.job;

import io.insight.real.city.dto.JobName;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobStrategyConfig {

    @Bean
    public JobStrategy cityBasicInfoStrategy(
            @Qualifier("updateCityBasicInfoJob") Job job,
            JobLauncher jobLauncher) {
        return new SimpleJobStrategyImpl(job, JobName.CITY_BASIC_INFO, jobLauncher);
    }

    @Bean
    public JobStrategy populationInfoStrategy(
            @Qualifier("updatePopulationInfoJob") Job job,
            JobLauncher jobLauncher) {
        return new SimpleJobStrategyImpl(job, JobName.POPULATION_INFO, jobLauncher);
    }

    @Bean
    public JobStrategy cityRankingStrategy(
            @Qualifier("cityRankingJob") Job job,
            JobLauncher jobLauncher) {
        return new SimpleJobStrategyImpl(job, JobName.CITY_RANKING, jobLauncher);
    }

    @Bean
    public JobStrategy popRankingStrategy(
            @Qualifier("popRankingJob") Job job,
            JobLauncher jobLauncher) {
        return new SimpleJobStrategyImpl(job, JobName.POPULATION_RANKING, jobLauncher);
    }

    @Bean
    public JobStrategy empJobStrategy(
            @Qualifier("updateCityEmpJob") Job job,
            JobLauncher jobLauncher) {
        return new SimpleJobStrategyImpl(job, JobName.CITY_EMPLOYMENT, jobLauncher);
    }
}
