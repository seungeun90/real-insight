package io.insight.real.infra.persistence.jpa;

import io.insight.real.infra.persistence.entity.CityBasicInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityDataRepository extends MongoRepository<CityBasicInfo, Long> {
    CityBasicInfo getCityBasicInfoByAdmCdAndYear(String admcd, String year);
}
