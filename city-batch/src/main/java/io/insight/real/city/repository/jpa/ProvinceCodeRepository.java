package io.insight.real.city.repository.jpa;

import io.insight.real.city.repository.entity.ProvinceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceCodeRepository extends JpaRepository<ProvinceCode, Long> {
}
