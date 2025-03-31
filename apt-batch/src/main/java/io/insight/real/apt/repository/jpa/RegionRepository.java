package io.insight.real.apt.repository.jpa;

import io.insight.real.apt.repository.jpa.entity.RegionJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<RegionJpa, Long> {
    List<RegionJpa> findAllByStatus(String status);
}
