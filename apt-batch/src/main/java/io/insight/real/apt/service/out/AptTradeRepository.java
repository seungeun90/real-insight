package io.insight.real.apt.service.out;

import io.insight.real.apt.repository.jpa.dao.AptPriceDao;

import java.util.List;

public interface AptTradeRepository {
    List<AptPriceDao> findMaxMinPrices(String regionCode, String areaRange);
}
