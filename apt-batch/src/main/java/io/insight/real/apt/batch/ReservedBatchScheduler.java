package io.insight.real.apt.batch;

import io.insight.real.apt.batch.job.JobTriggerService;
import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.repository.jpa.entity.BatchJobQueueJpa;
import io.insight.real.apt.repository.jpa.BatchJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservedBatchScheduler {
    private final BatchJobRepository batchJobRepository;
    private final JobTriggerService jobTriggerService;

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다
    public void triggerReservedJobs() {
        LocalDateTime now = LocalDateTime.now();
        // 예약 시간이 지난 READY 상태의 Job들 가져오기
        List<BatchJobQueueJpa> readyJobs = batchJobRepository.findReadyJobs(now);

        for (BatchJobQueueJpa job : readyJobs) {
            try {
                // 실행 전 상태 업데이트
                job.setStatus("IN_PROGRESS");
                batchJobRepository.save(job);

                BatchJobRequest batchJobRequest = BatchJobRequest.builder()
                        .jobId(job.getId())
                        .regionCode(job.getRegionCode())
                        .adres(job.getAddres())
                        .startDate(job.getStartDate())
                        .endDate(job.getEndDate())
                        .build();
                jobTriggerService.updateAptTradeJob(batchJobRequest);

                log.info("예약 배치 실행 완료: {}", job.getId());
            } catch (Exception e) {
                log.error(" 예약 배치 실행 실패: {}", job.getId(), e);
            }
        }
    }
}
