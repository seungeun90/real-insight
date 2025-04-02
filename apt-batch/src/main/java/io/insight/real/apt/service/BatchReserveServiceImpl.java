package io.insight.real.apt.service;


import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.JobName;
import io.insight.real.apt.dto.JobStatus;
import io.insight.real.apt.repository.jpa.BatchJobRepository;
import io.insight.real.apt.repository.jpa.entity.BatchJobQueueJpa;
import io.insight.real.apt.service.in.BatchReserveService;
import io.insight.real.apt.util.SlotDelayCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
public class BatchReserveServiceImpl implements BatchReserveService {

    private final BatchJobRepository batchJobRepository;
    private final AptInfoJobStrategyImpl aptInfoService;
    private final AptTradeJobStrategyImpl aptTradeJobService;

    @Transactional
    public void enqueueAptTradeJob(BatchJobRequest request) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int activeJobCount = batchJobRepository.countActiveJobs(now);
        LocalDateTime scheduledTime = SlotDelayCalculator.calculateScheduledTime(activeJobCount)
                .truncatedTo(ChronoUnit.MINUTES);


        // DB에 배치 Job 등록
        BatchJobQueueJpa entity = BatchJobQueueJpa.builder()
                .jobName(JobName.APT_TRADE_JOB.name())
                .status(JobStatus.READY.name())
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
        if (activeJobCount < 5 && scheduledTime.isBefore(LocalDateTime.now().plusSeconds(10))) {
            aptTradeJobService.triggerJob(request);
        }
    }

    @Transactional
    public void enqueueAptInfoJob(BatchJobRequest request) {
        int activeJobCount = batchJobRepository.countActiveJobs(LocalDateTime.now());
        LocalDateTime scheduledTime = SlotDelayCalculator.calculateScheduledTime(activeJobCount);

        // DB에 배치 Job 등록
        BatchJobQueueJpa entity = BatchJobQueueJpa.builder()
                .jobName(JobName.APT_INFO_JOB.name())
                .status(JobStatus.READY.name())
                .addres(request.getAdres())
                .updatedAt(LocalDateTime.now())
                .scheduledAt(scheduledTime)
                .build();

        BatchJobQueueJpa saved = batchJobRepository.save(entity);
        request.setJobId(saved.getId());
        request.setJobName(JobName.APT_INFO_JOB.name());

        if (activeJobCount < 5 && scheduledTime.isBefore(LocalDateTime.now().plusSeconds(10))) {
            aptInfoService.triggerJob(request);
        }
    }
}
