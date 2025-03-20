package io.insight.real.apt.batch.job;

import io.insight.real.apt.dto.BatchJobRequest;
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
    private final Job updateAptInfoJob;
    private final Job updateAptTradeJob;

    public JobTriggerService(
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            @Qualifier("updateAptInfoJob") Job updateAptInfoJob,
            @Qualifier("updateAptTradeJob") Job updateAptTradeJob
    ) {
        this.jobLauncher = jobLauncher;
        this.updateAptInfoJob = updateAptInfoJob;
        this.updateAptTradeJob = updateAptTradeJob;
    }

    public void updateAptInfoJob(BatchJobRequest request) throws JobExecutionException {
        log.info("updateAptInfoJob start==");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("jobId", request.getJobId())
                .addString("adres", request.getAdres())
                .addLong("timestamp", System.currentTimeMillis()) // 실행 시 중복 방지
                .toJobParameters();

        jobLauncher.run(updateAptInfoJob, jobParameters);
        log.info(" updateAptInfoJob end==");
    }

    public void updateAptTradeJob(BatchJobRequest request) throws JobExecutionException {
        log.info("updateAptTradeJob start==");
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("jobId", request.getJobId())
                .addString("regionCode", request.getRegionCode())
                .addString("startDate", request.getStartDate())
                .addString("endDate", request.getEndDate())
                .addLong("timestamp", System.currentTimeMillis()) // 실행 시 중복 방지
                .toJobParameters();

        jobLauncher.run(updateAptTradeJob, jobParameters);
        log.info(" updateAptTradeJob end==");
    }


}
