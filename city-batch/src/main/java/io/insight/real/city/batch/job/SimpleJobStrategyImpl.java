package io.insight.real.city.batch.job;

import io.insight.real.city.dto.JobName;
import io.insight.real.city.dto.request.BatchRunRequest;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

public class SimpleJobStrategyImpl implements JobStrategy {

    private final Job job;
    private final JobName jobName;
    private final JobLauncher jobLauncher;

    public SimpleJobStrategyImpl(Job job, JobName jobName, JobLauncher jobLauncher) {
        this.job = job;
        this.jobName = jobName;
        this.jobLauncher = jobLauncher;
    }

    @Override
    public void run(BatchRunRequest request) throws JobExecutionException {
        JobParameters params = new JobParametersBuilder()
                .addString("code", request.getCode())
                .addString("year", request.getYear())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(job, params);
    }

    @Override
    public JobName getJobName() {
        return jobName;
    }
}
