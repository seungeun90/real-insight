package io.insight.real.apt.repository.jpa;

import io.insight.real.apt.repository.jpa.entity.BatchJobQueueJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BatchJobRepository extends JpaRepository<BatchJobQueueJpa, Long> {
    @Query("SELECT COUNT(b) FROM BatchJobQueueJpa b WHERE b.status IN ('READY', 'IN_PROGRESS') AND b.scheduledAt <= :now")
    int countActiveJobs(@Param("now") LocalDateTime now);

    @Query("SELECT b FROM BatchJobQueueJpa b WHERE b.status IN ('READY', 'FAILED') AND b.scheduledAt <= :now")
    List<BatchJobQueueJpa> findReadyJobs(@Param("now") LocalDateTime now);
}
