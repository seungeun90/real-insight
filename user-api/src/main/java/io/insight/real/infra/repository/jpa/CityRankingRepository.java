package io.insight.real.infra.repository.jpa;

import io.insight.real.infra.repository.entity.CityRanking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityRankingRepository extends MongoRepository<CityRanking, Long> {

    CityRanking findByProvinceCodeAndCityCodeAndYear(String provinceCode, String cityCode, String year);

}
