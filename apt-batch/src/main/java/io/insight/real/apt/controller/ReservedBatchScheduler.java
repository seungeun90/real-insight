package io.insight.real.apt.controller;

import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.JobName;
import io.insight.real.apt.dto.JobStatus;
import io.insight.real.apt.repository.jpa.entity.BatchJobQueueJpa;
import io.insight.real.apt.service.BatchJobStatusService;
import io.insight.real.apt.service.in.JobStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservedBatchScheduler {

    private Map<JobName, JobStrategy> strategies;
    private BatchJobStatusService batchJobService;
    public ReservedBatchScheduler(
            BatchJobStatusService batchJobService,
            List<JobStrategy> strategies) {
        this.batchJobService = batchJobService;
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(JobStrategy::getJobType, Function.identity()));
    }

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다
    public void triggerReservedJobs() {
        // 예약 시간이 지난 READY 상태의 Job들 가져오기
        List<BatchJobQueueJpa> readyJobs = batchJobService.findTop5Jobs();

        for (BatchJobQueueJpa job : readyJobs) {
            try {
                // 실행 전 상태 업데이트
                batchJobService.saveStatus(job.getId(), JobStatus.IN_PROGRESS.name());

                BatchJobRequest batchJobRequest = BatchJobRequest.builder()
                        .jobId(job.getId())
                        .regionCode(job.getRegionCode())
                        .adres(job.getAddres())
                        .startDate(job.getStartDate())
                        .endDate(job.getEndDate())
                        .build();

                JobName jobName = JobName.valueOf(job.getJobName());
                JobStrategy jobStrategy = strategies.get(jobName);
                jobStrategy.triggerJob(batchJobRequest);

                log.info("예약 배치 실행 완료: {}, {}, {}", job.getId(), job.getJobName(), job.getAddres());
            } catch (Exception e) {
                log.error(" 예약 배치 실행 실패:{}, {}, {}", job.getId(), job.getJobName(), job.getAddres(), e);
            }
        }
    }
}
