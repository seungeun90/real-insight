package io.insight.real.city.repository.jpa;

import io.insight.real.city.repository.entity.CityBasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityBasicRepository extends JpaRepository<CityBasicInfo, Long> {

    List<CityBasicInfo> findByAdmCd(String admCd);
    CityBasicInfo findByAdmCdAndYear(String admCd, String year);
}
