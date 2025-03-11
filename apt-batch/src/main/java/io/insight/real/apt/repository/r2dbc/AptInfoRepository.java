package io.insight.real.apt.repository.r2dbc;

import io.insight.real.apt.repository.entity.AptInfo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AptInfoRepository extends ReactiveCrudRepository<AptInfo, Long> {



}
