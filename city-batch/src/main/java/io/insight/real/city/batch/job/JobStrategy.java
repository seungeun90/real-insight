package io.insight.real.city.batch.job;

import io.insight.real.city.dto.JobName;
import io.insight.real.city.dto.request.BatchRunRequest;
import org.springframework.batch.core.JobExecutionException;

public interface JobStrategy {
    void run(BatchRunRequest request) throws JobExecutionException;
    JobName getJobName();
}
