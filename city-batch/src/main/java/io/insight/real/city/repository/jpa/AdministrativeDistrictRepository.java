package io.insight.real.city.repository.jpa;

import io.insight.real.city.repository.entity.AdministrativeDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdministrativeDistrictRepository extends JpaRepository<AdministrativeDistrict, Long> {
    List<AdministrativeDistrict> findByProvinceCode(String provinceCode);


    @Query("SELECT DISTINCT a.provinceCode FROM AdministrativeDistrict a")
    List<String> findDistinctProvinceCodes();
}
