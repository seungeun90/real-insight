package io.insight.real.apt.service;


import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.JobName;
import io.insight.real.apt.repository.jpa.entity.BatchJobQueueJpa;
import io.insight.real.apt.repository.jpa.BatchJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class BatchReserveService {

    private final BatchJobRepository batchJobRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AptInfoJobService aptInfoService;

    @Transactional
    public void enqueueAptTradeJob(BatchJobRequest request) {
        int activeJobCount = batchJobRepository.countActiveJobs(LocalDateTime.now());
        LocalDateTime scheduledTime = SlotDelayCalculator.calculateScheduledTime(activeJobCount);

        // DB에 배치 Job 등록
        BatchJobQueueJpa entity = BatchJobQueueJpa.builder()
                .jobName(JobName.APT_TRADE_JOB.name())
                .status("READY")
                .regionCode(request.getRegionCode())
                .startDate(request.getStartDate())
                .addres(request.getAdres())
                .endDate(request.getEndDate())
                .updatedAt(LocalDateTime.now())
                .scheduledAt(scheduledTime)
                .build();

        BatchJobQueueJpa saved = batchJobRepository.save(entity);
        request.setJobId(saved.getId());
        request.setJobName(JobName.APT_TRADE_JOB.name());
        if (scheduledTime.isBefore(LocalDateTime.now().plusSeconds(10))) {

           // eventPublisher.publishEvent(request);
        }
    }

    @Transactional
    public void enqueueAptInfoJob(BatchJobRequest request) {
        int activeJobCount = batchJobRepository.countActiveJobs(LocalDateTime.now());
        LocalDateTime scheduledTime = SlotDelayCalculator.calculateScheduledTime(activeJobCount);

        // DB에 배치 Job 등록
        BatchJobQueueJpa entity = BatchJobQueueJpa.builder()
                .jobName(JobName.APT_INFO_JOB.name())
                .status("READY")
                .addres(request.getAdres())
                .updatedAt(LocalDateTime.now())
                .scheduledAt(scheduledTime)
                .build();

        BatchJobQueueJpa saved = batchJobRepository.save(entity);
        request.setJobId(saved.getId());
        request.setJobName(JobName.APT_INFO_JOB.name());

        if (scheduledTime.isBefore(LocalDateTime.now().plusSeconds(10))) {
            aptInfoService.triggerJob(request.getAdres(), saved.getId());
            //eventPublisher.publishEvent(request);
        }
    }
}
