package io.insight.real.city.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.insight.real.city.dto.CityBasicDto;
import io.insight.real.city.service.out.CityInfoSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static io.insight.real.city.repository.entity.QCityBasicInfo.cityBasicInfo;

@RequiredArgsConstructor
@Service
public class CityInfoSearchRepositoryImpl implements CityInfoSearchRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 도시 기본 정보를 시/군, 년도를 조건으로 조회
     * ex) [서울시 강남구, 서울시 종로구,..] 정보 조회
     * */
    public List<CityBasicDto> groupByAdmCd(String code, String year) {
        return queryFactory
                .select(Projections.constructor(CityBasicDto.class,
                        cityBasicInfo.provinceCode,
                        cityBasicInfo.cityCode,
                        cityBasicInfo.year,
                        cityBasicInfo.totalPopulation,
                        cityBasicInfo.populationDensity,
                        cityBasicInfo.agingChildIndex,
                        cityBasicInfo.householdCount,
                        cityBasicInfo.averageHouseholdSize,
                        cityBasicInfo.employCnt,
                        cityBasicInfo.corpCnt
                ))
                .from(cityBasicInfo)
                .where(
                        eqProvinceCode(code),
                        eqYear(year),
                        cityBasicInfo.cityCode.isNotEmpty(),
                        cityBasicInfo.townCode.isEmpty()
                )
                .fetch();
    }

    private BooleanExpression eqProvinceCode(final String code){
        return StringUtils.hasText(code) ? cityBasicInfo.provinceCode.eq(code) : null;
    }

    private BooleanExpression eqYear(final String year){
        return StringUtils.hasText(year) ? cityBasicInfo.year.eq(year) : null;
    }

}
