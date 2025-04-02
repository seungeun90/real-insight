package io.insight.real.city.batch.job.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerConfig {
    private final JobLauncher jobLauncher;
    private final Job updateCityBasicInfoJob;
    private final Job updatePopulationInfoJob;
    private final JobExplorer jobExplorer;

    public SchedulerConfig(JobLauncher jobLauncher,
                           @Qualifier("updateCityBasicInfoJob") Job updateCityBasicInfoJob,
                           @Qualifier("updatePopulationInfoJob") Job updatePopulationInfoJob,
                           JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.updateCityBasicInfoJob = updateCityBasicInfoJob;
        this.updatePopulationInfoJob = updatePopulationInfoJob;
        this.jobExplorer = jobExplorer;
    }
   // @Scheduled(cron = "0 31 22 * * ?")
//    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    public void runCityBasicInfoBatch() throws Exception {
        if (isJobRunning("updateCityBasicInfoJob")) {  // ✅ 실행 중인 Job이 있으면 실행하지 않음
            log.warn("🚨 Job이 이미 실행 중이므로 새로운 Job 실행을 방지합니다.");
            return;
        }
        try {
            log.info("updateCityBasicInfoJob start==");
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("code", "11")
                    .toJobParameters();

            jobLauncher.run(updateCityBasicInfoJob, parameters);
            log.info(" updateCityBasicInfoJob end==");
        } catch (Exception e) {
            log.error("e = {}" ,e);
        }
    }
    //@Scheduled(cron = "0 51 19 * * ?")
    public void runPopulationInfoBatchJob() throws Exception {
        if (isJobRunning("updatePopulationInfoJob")) {  // 실행 중인 Job이 있으면 실행하지 않음
            log.warn("🚨 Job이 이미 실행 중이므로 새로운 Job 실행을 방지합니다.");
            return;
        }
        try {
            log.info("updatePopulationInfoJob start==");
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("code", "11")
                    .toJobParameters();

            jobLauncher.run(updatePopulationInfoJob, parameters);
            log.info(" updatePopulationInfoJob end==");
        } catch (Exception e) {
            log.error("e = {}" ,e);
        }
    }

    private boolean isJobRunning(String jobName) {
        return jobExplorer.findRunningJobExecutions(jobName).size() > 0;
    }
}
