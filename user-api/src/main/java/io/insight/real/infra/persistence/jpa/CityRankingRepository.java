package io.insight.real.infra.persistence.jpa;

import io.insight.real.infra.persistence.entity.CityRanking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityRankingRepository extends MongoRepository<CityRanking, Long> {

    CityRanking findByProvinceCodeAndCityCodeAndYear(String provinceCode, String cityCode, String year);

}
