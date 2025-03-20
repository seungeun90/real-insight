package io.insight.real.apt.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "batch_job")
public class BatchJobQueueJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name")
    private String jobName;
    @Column(name = "addres")
    private String addres;
    @Column(name = "region_code")
    private String regionCode;
    @Column(name = "start_date")
    private String startDate;
    @Column(name = "end_date")
    private String endDate;
    @Column(name = "status")
    private String status;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
