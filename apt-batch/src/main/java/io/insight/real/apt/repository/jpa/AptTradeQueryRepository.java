package io.insight.real.apt.repository.jpa;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.insight.real.apt.dto.SupplyAreaRange;
import io.insight.real.apt.dto.response.AptTradeProfit;
import io.insight.real.apt.dto.response.TradeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.insight.real.apt.repository.jpa.entity.QAptInfoJpa.aptInfoJpa;
import static io.insight.real.apt.repository.jpa.entity.QAptTradeJpa.aptTradeJpa;

@RequiredArgsConstructor
@Repository
public class AptTradeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 21.01 ~ 22.12 최고가 & 21.01~오늘 최저가 조회 + 수익률 계산
     */
    public List<AptTradeProfit> findMaxMinPrices(String regionCode, String areaRange) {

        SupplyAreaRange range = SupplyAreaRange.getRange(areaRange);
        NumberExpression<Integer> maxPriceExp = new CaseBuilder()
                .when(aptTradeJpa.dealYear.between(2021, 2022))
                .then(Expressions.stringTemplate("REPLACE({0}, ',', '')", aptTradeJpa.dealAmount)
                        .castToNum(Integer.class))
                .otherwise(0)
                .max(); // 21~22년 최고 거래가

        NumberExpression<Integer> minPriceExp = new CaseBuilder()
                .when(aptTradeJpa.dealYear.goe(2021))
                .then(Expressions.stringTemplate("REPLACE({0}, ',', '')", aptTradeJpa.dealAmount)
                        .castToNum(Integer.class))
                .otherwise(0)
                .min();

        List<AptTradeProfit> priceResults = queryFactory
                .select(Projections.constructor(AptTradeProfit.class,
                        aptTradeJpa.id,
                        aptTradeJpa.aptNm,
                        maxPriceExp,
                        minPriceExp
                ))
                .from(aptTradeJpa)
                .join(aptInfoJpa).on(aptTradeJpa.aptNm.eq(aptInfoJpa.complexGbNm1))
                .where(aptTradeJpa.dealYear.goe(2021)
                        .and(aptInfoJpa.unitCnt.gt(200))
                        .and(aptTradeJpa.sggCd.eq(regionCode))
                        .and(aptTradeJpa.excluUseAr.between(range.getStart(), range.getEnd()))
                )
                .groupBy(aptTradeJpa.id,aptTradeJpa.aptNm)
                .orderBy(maxPriceExp.desc())
                .fetch();

        List<Long> list = priceResults.stream().map(AptTradeProfit::getId).toList();
        List<AptPriceDao> results = queryFactory
                .select(Projections.constructor(AptPriceDao.class,
                        aptTradeJpa.id,
                        aptTradeJpa.aptNm,
                        aptTradeJpa.umdNm,
                        aptTradeJpa.buildYear,
                        aptTradeJpa.dealYear,
                        aptTradeJpa.dealMonth,
                        aptTradeJpa.dealDay,
                        aptTradeJpa.floor,
                        aptTradeJpa.dealAmount,
                        aptTradeJpa.excluUseAr
                ))
                .from(aptTradeJpa)
                .where(aptTradeJpa.id.in(list))
                .fetch();

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