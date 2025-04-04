package io.insight.real.infra.persistence.jpa;

import io.insight.real.infra.persistence.entity.Employment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmploymentRepository extends MongoRepository<Employment, Long> {
}
