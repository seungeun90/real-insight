package io.insight.real.apt.service.in;

import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.JobName;

public interface JobStrategy {
    void triggerJob(BatchJobRequest request);
    JobName getJobType();
}
