package io.insight.real.apt.controller;

import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.JobName;
import io.insight.real.apt.dto.JobStatus;
import io.insight.real.apt.repository.jpa.BatchJobRepository;
import io.insight.real.apt.repository.jpa.entity.BatchJobQueueJpa;
import io.insight.real.apt.service.AptInfoJobService;
import io.insight.real.apt.service.AptTradeJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservedBatchScheduler {
    private final BatchJobRepository batchJobRepository;
    private final AptInfoJobService aptInfoService;
    private final AptTradeJobService aptTradeJobService;

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다
    public void triggerReservedJobs() {
        LocalDateTime now = LocalDateTime.now();
        // 예약 시간이 지난 READY 상태의 Job들 가져오기
        List<BatchJobQueueJpa> readyJobs = batchJobRepository.findTop5Jobs(LocalDateTime.now(), PageRequest.of(0, 5));

        for (BatchJobQueueJpa job : readyJobs) {
            try {
                // 실행 전 상태 업데이트
                job.setStatus(JobStatus.IN_PROGRESS.name());
                batchJobRepository.save(job);

                if(JobName.APT_TRADE_JOB.name().equals(job.getJobName())) {
                    BatchJobRequest batchJobRequest = BatchJobRequest.builder()
                            .jobId(job.getId())
                            .regionCode(job.getRegionCode())
                            .adres(job.getAddres())
                            .startDate(job.getStartDate())
                            .endDate(job.getEndDate())
                            .build();
                    aptTradeJobService.triggerJob(batchJobRequest);
                }
                if(JobName.APT_INFO_JOB.name().equals(job.getJobName())) {
                    aptInfoService.triggerJob(job.getAddres(), job.getId());
                }

                log.info("예약 배치 실행 완료: {}, {}, {}", job.getId(), job.getJobName(), job.getAddres());
            } catch (Exception e) {
                log.error(" 예약 배치 실행 실패:{}, {}, {}", job.getId(), job.getJobName(), job.getAddres(), e);
            }
        }
    }
}
