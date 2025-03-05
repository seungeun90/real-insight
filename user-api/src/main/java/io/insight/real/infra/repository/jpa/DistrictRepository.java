package io.insight.real.infra.repository.jpa;

import io.insight.real.infra.repository.entity.District;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DistrictRepository extends MongoRepository<District, String> {
}
