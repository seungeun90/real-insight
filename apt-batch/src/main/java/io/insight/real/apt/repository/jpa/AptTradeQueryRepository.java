package io.insight.real.apt.repository.jpa;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.insight.real.apt.dto.AptTradeProfit;
import io.insight.real.apt.dto.SupplyAreaRange;
import io.insight.real.apt.repository.jpa.dao.AptPriceDao;
import io.insight.real.apt.service.out.AptTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.insight.real.apt.repository.jpa.entity.QAptInfoJpa.aptInfoJpa;
import static io.insight.real.apt.repository.jpa.entity.QAptTradeJpa.aptTradeJpa;

@RequiredArgsConstructor
@Repository
public class AptTradeQueryRepository implements AptTradeRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 21.01 ~ 22.12 최고가 & 21.01~오늘 최저가 조회 + 수익률 계산
     */
    @Override
    public List<AptPriceDao> findMaxMinPrices(String regionCode, String areaRange) {

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
        return queryFactory
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
    }
}