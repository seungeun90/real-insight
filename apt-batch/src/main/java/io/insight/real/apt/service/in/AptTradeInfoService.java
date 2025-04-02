package io.insight.real.apt.service.in;

import io.insight.real.apt.dto.AptTradeProfit;

import java.util.List;

public interface AptTradeInfoService {
    List<AptTradeProfit> getAptTradeInfo(String regionCode, String areaSize);
}
