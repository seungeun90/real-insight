package io.insight.real.infra.persistence.jpa;

import io.insight.real.infra.persistence.entity.CityPopulation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PopulationDataRepository extends MongoRepository<CityPopulation, Long> {
}
