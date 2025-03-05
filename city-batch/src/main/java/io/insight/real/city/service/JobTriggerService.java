package io.insight.real.city.service;

import io.insight.real.city.dto.request.BatchRunRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobTriggerService {
    private final JobLauncher jobLauncher;
    private final Job updateCityBasicInfoJob;
    private final Job updatePopulationInfoJob;
    private final Job cityRankingJob;
    private final Job popRankingJob;
    private final Job cityEmpJob;

    public JobTriggerService(
            JobLauncher jobLauncher,
            @Qualifier("updateCityBasicInfoJob") Job updateCityBasicInfoJob,
            @Qualifier("updatePopulationInfoJob") Job updatePopulationInfoJob,
            @Qualifier("cityRankingJob") Job cityRankingJob,
            @Qualifier("popRankingJob") Job popRankingJob,
            @Qualifier("updateCityEmpJob") Job cityEmpJob
    ) {
        this.jobLauncher = jobLauncher;
        this.updateCityBasicInfoJob = updateCityBasicInfoJob;
        this.updatePopulationInfoJob = updatePopulationInfoJob;
        this.cityRankingJob = cityRankingJob;
        this.popRankingJob = popRankingJob;
        this.cityEmpJob = cityEmpJob;
    }

    public void runUpdatePopulationInfoJob(BatchRunRequest request) throws JobExecutionException {
        log.info("updatePopulationInfoJob start==");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("code", request.getCode())
                .addString("year", request.getYear())
                .addLong("timestamp", System.currentTimeMillis()) // 실행 시 중복 방지
                .toJobParameters();

        jobLauncher.run(updatePopulationInfoJob, jobParameters);
        log.info(" updatePopulationInfoJob end==");
    }

    public void runUpdateCityBasicInfoJob(BatchRunRequest request) throws JobExecutionException {
        log.info("updateCityBasicInfoJob start==");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("code", request.getCode())
                .addString("year", request.getYear())
                .addLong("timestamp", System.currentTimeMillis()) // 실행 시 중복 방지
                .toJobParameters();

        jobLauncher.run(updateCityBasicInfoJob, jobParameters);
        log.info(" updateCityBasicInfoJob end==");
    }

    public void runUpdateCityRankingJob(BatchRunRequest request) throws JobExecutionException {
        log.info("runUpdateCityRankingJob start==");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("code", request.getCode())
                .addString("year", request.getYear())
                .addLong("timestamp", System.currentTimeMillis()) // 실행 시 중복 방지
                .toJobParameters();

        jobLauncher.run(cityRankingJob, jobParameters);
        log.info("UpdateCityRankingJob end==");
    }

    public void runUpdatePopulationRankJob(BatchRunRequest request) throws JobExecutionException {
        log.info("runUpdatePopulationRankJob start==");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("code", request.getCode())
                .addLong("timestamp", System.currentTimeMillis()) // 실행 시 중복 방지
                .toJobParameters();

        jobLauncher.run(popRankingJob, jobParameters);
        log.info(" runUpdatePopulationRankJob end==");
    }

    public void runUpdateEmpJob(BatchRunRequest request) throws JobExecutionException {
        log.info("runUpdateEmpJob start==");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("code", request.getCode())
                .addString("year", request.getYear())
                .addLong("timestamp", System.currentTimeMillis()) // 실행 시 중복 방지
                .toJobParameters();

        jobLauncher.run(cityEmpJob, jobParameters);
        log.info(" runUpdateEmpJob end==");
    }
}
