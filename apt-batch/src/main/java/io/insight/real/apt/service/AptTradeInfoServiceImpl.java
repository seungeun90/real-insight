package io.insight.real.apt.service;

import io.insight.real.apt.dto.AptTradeProfit;
import io.insight.real.apt.dto.TradeInfo;
import io.insight.real.apt.repository.jpa.AptTradeQueryRepository;
import io.insight.real.apt.repository.jpa.dao.AptPriceDao;
import io.insight.real.apt.service.in.AptTradeInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AptTradeInfoServiceImpl implements AptTradeInfoService {
    private final AptTradeQueryRepository aptTradeQueryRepository;

    @Override
    public List<AptTradeProfit> getAptTradeInfo(String regionCode, String areaSize){
        List<AptPriceDao> results = aptTradeQueryRepository.findMaxMinPrices(regionCode,areaSize);
        Map<String, List<AptPriceDao>> groupedByAptNm = results.stream()
                .collect(Collectors.groupingBy(AptPriceDao::getAptNm));

        List<AptTradeProfit> tradeProfits = groupedByAptNm.entrySet().stream()
                .map(entry -> {
                    String aptNm = entry.getKey();
                    List<AptPriceDao> priceList = entry.getValue();


                    AptPriceDao maxPriceData = priceList.stream()
                            .max(Comparator.comparingInt(aptPriceDao -> Integer.parseInt(aptPriceDao.getDealAmount().replace(",", ""))))
                            .orElse(null);

                    AptPriceDao minPriceData = priceList.stream()
                            .min(Comparator.comparingInt(aptPriceDao -> Integer.parseInt(aptPriceDao.getDealAmount().replace(",", ""))))
                            .orElse(null);
                    Integer maxPrice = (maxPriceData != null) ? Integer.parseInt(maxPriceData.getDealAmount().replace(",", "")) : 0;
                    Integer minPrice = (minPriceData != null) ? Integer.parseInt(minPriceData.getDealAmount().replace(",", "")) : 0;

                    String maxDealDate = (maxPriceData != null)
                            ? String.format("%02d.%02d.%02d", maxPriceData.getDealYear() % 100, maxPriceData.getDealMonth(), maxPriceData.getDealDay())
                            : "N/A";

                    String minDealDate = (minPriceData != null)
                            ? String.format("%02d.%02d.%02d", minPriceData.getDealYear() % 100, minPriceData.getDealMonth(), minPriceData.getDealDay())
                            : "N/A";


                    TradeInfo maxTrade = TradeInfo.builder()
                            .price(maxPrice)
                            .excluAr(maxPriceData.getExcluUseAr())
                            .dealDate(maxDealDate)
                            .floor(maxPriceData.getFloor())
                            .build();
                    TradeInfo minTrade = TradeInfo.builder()
                            .price(minPrice)
                            .excluAr(minPriceData.getExcluUseAr())
                            .dealDate(minDealDate)
                            .floor(minPriceData.getFloor())
                            .build();
                    double profitRate = (minPrice > 0)
                            ? ((double) (maxPrice - minPrice) / minPrice) * 100
                            : 0;
                    BigDecimal roundedProfitRate = BigDecimal.valueOf(profitRate)
                            .setScale(2, RoundingMode.HALF_UP);

                    return AptTradeProfit.builder()
                            .aptNm(aptNm)
                            .umdNm(maxPriceData.getUmdNm())
                            .buildYear(maxPriceData.getBuildYear())
                            .maxTrade(maxTrade)
                            .minTrade(minTrade)
                            .profitRate(roundedProfitRate.doubleValue())
                            .build();
                })
                .toList();
        return tradeProfits;
    }
}
