package io.insight.real.infra.repository.jpa;

import io.insight.real.infra.repository.entity.CityPopulation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PopulationDataRepository extends MongoRepository<CityPopulation, Long> {
}
