package io.insight.real.city.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.insight.real.city.repository.dao.PopulationDao;
import io.insight.real.city.service.out.PopInfoSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static io.insight.real.city.repository.entity.QAdministrativeDistrict.administrativeDistrict;
import static io.insight.real.city.repository.entity.QCityPopulation.cityPopulation;

@RequiredArgsConstructor
@Repository
public class PopInfoSearchRepositoryImpl implements PopInfoSearchRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 도시 기본 정보를 시/군, 년도를 조건으로 조회
     * ex) [서울시 강남구, 서울시 종로구,..] 정보 조회
     * */
    @Override
    public List<PopulationDao> groupByAdmCd(String code) {
        return queryFactory
                .select(Projections.constructor(PopulationDao.class,
                        cityPopulation.provinceCode,
                        cityPopulation.cityCode,
                        cityPopulation.townCode,
                        administrativeDistrict.townName,
                        cityPopulation.teenageLessThanPer,
                        cityPopulation.teenagePer,
                        cityPopulation.twentyPer,
                        cityPopulation.thirtyPer,
                        cityPopulation.fortyPer,
                        cityPopulation.fiftyPer,
                        cityPopulation.sixtyPer,
                        cityPopulation.seventyMoreThanPer
                ))
                .from(cityPopulation)
                .leftJoin(administrativeDistrict)
                .on(cityPopulation.provinceCode.eq(administrativeDistrict.provinceCode)
                        .and(cityPopulation.cityCode.eq(administrativeDistrict.cityDistrictCode))
                        .and(cityPopulation.townCode.eq(administrativeDistrict.townCode)))

                .where(
                        eqProvinceCode(code),
                        cityPopulation.cityCode.isNotEmpty(),
                        cityPopulation.townCode.isNotEmpty()
                )
                .fetch();
    }


    private BooleanExpression eqProvinceCode(final String code){
        return StringUtils.hasText(code) ? cityPopulation.provinceCode.eq(code) : null;
    }
}
