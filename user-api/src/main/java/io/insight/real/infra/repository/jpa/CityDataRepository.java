package io.insight.real.infra.repository.jpa;

import io.insight.real.infra.repository.entity.CityBasicInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityDataRepository extends MongoRepository<CityBasicInfo, Long> {
    CityBasicInfo getCityBasicInfoByAdmCdAndYear(String admcd, String year);
}
