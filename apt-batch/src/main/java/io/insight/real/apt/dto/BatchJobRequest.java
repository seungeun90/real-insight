package io.insight.real.apt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter

@NoArgsConstructor
public class BatchJobRequest {
    private Long jobId;
    private String jobName;
    private String adres;
    private String regionCode;
    private String startDate;
    private String endDate;
    private LocalDateTime scheduledAt;

    @Builder
    public BatchJobRequest(Long jobId,
                           String jobName,
                           String adres,
                           String regionCode,
                           String startDate,
                           String endDate,
                           LocalDateTime scheduledAt) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.adres = adres;
        this.regionCode = regionCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.scheduledAt = scheduledAt;

    }


}
