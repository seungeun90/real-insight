package io.insight.real.apt.repository.jpa;

import io.insight.real.apt.repository.jpa.entity.ProvinceJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceJpa, Long> {
}
