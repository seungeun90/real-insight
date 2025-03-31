package io.insight.real.apt.service;

import io.insight.real.apt.repository.jpa.BatchJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BatchJobStatusService {

    private final BatchJobRepository batchJobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveStatus(Long jobId, String status) {
        batchJobRepository.findById(jobId).ifPresent(batchJob -> {
            batchJob.setStatus(status);
            batchJobRepository.save(batchJob);
        });
    }

}
