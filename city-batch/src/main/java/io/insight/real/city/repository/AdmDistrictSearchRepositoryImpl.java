package io.insight.real.city.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.insight.real.city.repository.entity.AdministrativeDistrict;
import io.insight.real.city.service.out.AdmDistrictSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.insight.real.city.repository.entity.QAdministrativeDistrict.administrativeDistrict;
import static io.insight.real.city.repository.entity.QCityPopulation.cityPopulation;


@RequiredArgsConstructor
@Service
public class AdmDistrictSearchRepositoryImpl implements AdmDistrictSearchRepository {
    private final JPAQueryFactory queryFactory;


    /**
     * SELECT ad.*
     * FROM adm_district ad
     * LEFT JOIN city_population cp
     *     ON cp.adm_cd =
     *         CONCAT(ad.province_code, ad.city_district_code, ad.town_code)
     * WHERE cp.adm_cd IS NULL
     * AND ad.province_code = :code
     *
     *
     * */
    public List<AdministrativeDistrict> findMissingRecords(String code) {
        return queryFactory
                .selectFrom(administrativeDistrict)
                .leftJoin(cityPopulation)
                .on(cityPopulation.admCd.eq(
                        administrativeDistrict.provinceCode
                                .concat(administrativeDistrict.cityDistrictCode)
                                .concat(administrativeDistrict.townCode)
                ))
                .where(cityPopulation.admCd.isNull())
                .where(administrativeDistrict.provinceCode.eq(code))
                .fetch();

    }

}
