package io.insight.real.infra.repository.jpa;

import io.insight.real.infra.repository.entity.Employment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmploymentRepository extends MongoRepository<Employment, Long> {
}
