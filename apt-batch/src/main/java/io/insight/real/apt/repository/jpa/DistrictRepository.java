package io.insight.real.apt.repository.jpa;

import io.insight.real.apt.repository.jpa.entity.LegalDistrictJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<LegalDistrictJpa, Long> {


}
