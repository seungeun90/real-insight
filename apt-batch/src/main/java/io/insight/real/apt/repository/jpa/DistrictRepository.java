package io.insight.real.apt.repository.jpa;

import io.insight.real.apt.repository.jpa.entity.LegalDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<LegalDistrict, Long> {


}
