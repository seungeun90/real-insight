package io.insight.real.apt.service;

import io.insight.real.apt.batch.job.JobTriggerService;
import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.JobName;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class BatchJobTriggerListener {
    private final JobTriggerService jobTriggerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBatchQueued(BatchJobRequest request) throws JobExecutionException {
        if(JobName.APT_TRADE_JOB.name().equals(request.getJobName())) {
            jobTriggerService.updateAptTradeJob(request);
        } else if (JobName.APT_INFO_JOB.name().equals(request.getJobName())){
            jobTriggerService.updateAptInfoJob(request);
        }

    }


}
