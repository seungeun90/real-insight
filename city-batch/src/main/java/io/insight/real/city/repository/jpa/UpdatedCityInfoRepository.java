package io.insight.real.city.repository.jpa;

import io.insight.real.city.repository.entity.UpdatedCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpdatedCityInfoRepository extends JpaRepository<UpdatedCity, Long> {
    List<UpdatedCity> findByAdmCd(String amdCd);
}
