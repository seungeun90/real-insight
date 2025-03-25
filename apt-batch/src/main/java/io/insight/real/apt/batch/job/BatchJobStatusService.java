package io.insight.real.apt.batch.job;

import io.insight.real.apt.repository.jpa.BatchJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BatchJobStatusService {
//implements JobExecutionListener {

    private final BatchJobRepository batchJobRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveStatus(Long jobId, String status) {
        batchJobRepository.findById(jobId).ifPresent(batchJob -> {
            batchJob.setStatus(status);
            batchJobRepository.save(batchJob);
        });
    }

  /*  @Transactional
    @Override
    public void afterJob(JobExecution jobExecution) {
        Long jobId = jobExecution.getJobParameters().getLong("jobId");
        if(jobId == null) {
            log.info("job id is null");
            return;}
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            batchJobRepository.findById(jobId).ifPresent(batchJob -> {
                batchJob.setStatus("DONE");
                batchJobRepository.save(batchJob);
            });

        } else {
            batchJobRepository.findById(jobId).ifPresent(batchJob -> {
                batchJob.setStatus("FAILED");
                batchJobRepository.save(batchJob);
            });
        }
    }*/
}
