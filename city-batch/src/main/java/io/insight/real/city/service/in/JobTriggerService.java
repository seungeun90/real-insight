package io.insight.real.city.service.in;

import io.insight.real.city.dto.JobName;
import io.insight.real.city.dto.request.BatchRunRequest;
import org.springframework.batch.core.JobExecutionException;

public interface JobTriggerService {
    void run(JobName jobName, BatchRunRequest request) throws JobExecutionException;
}
