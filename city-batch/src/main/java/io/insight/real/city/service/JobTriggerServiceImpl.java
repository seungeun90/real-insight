package io.insight.real.city.service;

import io.insight.real.city.dto.JobName;
import io.insight.real.city.dto.request.BatchRunRequest;
import io.insight.real.city.batch.job.JobStrategy;
import io.insight.real.city.service.in.JobTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobTriggerServiceImpl implements JobTriggerService {

    private final Map<JobName, JobStrategy> jobStrategies;

    public JobTriggerServiceImpl(
            List<JobStrategy> jobStrategies
    ){
        this.jobStrategies = jobStrategies.stream()
                .collect(Collectors.toMap(JobStrategy::getJobName, Function.identity()));
    }
    @Override
    public void run(JobName jobName, BatchRunRequest request) throws JobExecutionException{
        JobStrategy jobStrategy = jobStrategies.get(jobName);
        if (jobStrategy == null) {
            throw new IllegalArgumentException("JobStrategy not found for jobName: " + jobName);
        }
        jobStrategy.run(request);
    }
}
