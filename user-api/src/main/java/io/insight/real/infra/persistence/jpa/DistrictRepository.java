package io.insight.real.infra.persistence.jpa;

import io.insight.real.infra.persistence.entity.District;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DistrictRepository extends MongoRepository<District, String> {
}
