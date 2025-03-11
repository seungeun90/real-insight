package io.insight.real.apt.batch.job;

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

    public JobTriggerService(
            @Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
            @Qualifier("updateAptInfoJob") Job updateAptInfoJob

    ) {
        this.jobLauncher = jobLauncher;
        this.updateAptInfoJob = updateAptInfoJob;
    }

    public void updateAptInfoJob(AptItemRequest request) throws JobExecutionException {
        log.info("updateAptInfoJob start==");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("adres", request.getAdres())
                .addLong("timestamp", System.currentTimeMillis()) // 실행 시 중복 방지
                .toJobParameters();

        jobLauncher.run(updateAptInfoJob, jobParameters);
        log.info(" updateAptInfoJob end==");
    }


}
