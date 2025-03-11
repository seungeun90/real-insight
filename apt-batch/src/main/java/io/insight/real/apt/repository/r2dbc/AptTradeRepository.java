package io.insight.real.apt.repository.r2dbc;

import io.insight.real.apt.repository.entity.AptTrade;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AptTradeRepository extends ReactiveCrudRepository<AptTrade, Long> {
}
