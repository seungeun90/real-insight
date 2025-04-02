package io.insight.real.apt.service;

import io.insight.real.apt.repository.jpa.BatchJobRepository;
import io.insight.real.apt.repository.jpa.entity.BatchJobQueueJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<BatchJobQueueJpa> findTop5Jobs() {
        return batchJobRepository.findTop5Jobs(LocalDateTime.now(), PageRequest.of(0, 5));
    }
}
