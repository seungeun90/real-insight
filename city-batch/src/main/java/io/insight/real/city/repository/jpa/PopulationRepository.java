package io.insight.real.city.repository.jpa;

import io.insight.real.city.repository.entity.CityPopulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopulationRepository extends JpaRepository<CityPopulation, Long> {
    CityPopulation findByAdmCd(String code);
    boolean existsByAdmCd(String admCd);


}
