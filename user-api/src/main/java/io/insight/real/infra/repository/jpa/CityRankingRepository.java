package io.insight.real.infra.repository.jpa;

import io.insight.real.dto.CityRankingData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityRankingRepository extends MongoRepository<CityRankingData, Long> {

    CityRankingData findByProvinceCodeAndCityCodeAndYear(String provinceCode,String cityCode,String year);

}
