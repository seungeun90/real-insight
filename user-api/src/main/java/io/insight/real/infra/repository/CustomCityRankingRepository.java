package io.insight.real.infra.repository;

import io.insight.real.dto.CityBasicData;
import io.insight.real.dto.CityPopulationData;
import io.insight.real.dto.PopRankingData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.repository.entity.CityPopulation;
import io.insight.real.infra.repository.entity.District;
import io.insight.real.infra.repository.entity.Employment;
import io.insight.real.infra.repository.jpa.CityRankingRepository;
import io.insight.real.service.out.CityRankingSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class CustomCityRankingRepository implements CityRankingSearchRepository {
    private final MongoTemplate mongoTemplate;
    private final CityRankingRepository cityRankingRepository;
    /**
     * 지역 기초 정보 조회
     * */
    @Override
    public CityBasicData findCityData(String provinceCode, String cityCode, String year) {
        Query common = new Query();
        if (provinceCode != null) {
            common.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        }
        if (cityCode != null) {
            common.addCriteria(Criteria.where("cityCode").is(cityCode));
        }
        if (year != null) {
            common.addCriteria(Criteria.where("year").is(year));
        }

        CityBasicInfo cityData = getCityData(common);
        CityRankingData cityRankingData = getCityRankingData(common);

        CityRankingData workRank = cityRankingRepository.findByProvinceCodeAndCityCodeAndYear(provinceCode, cityCode, "2022");
        if(workRank != null) {
            cityRankingData.setEmployCntRank(workRank.getEmployCntRank());
            cityRankingData.setCorpCntRank(workRank.getCorpCntRank());
        }
        return CityBasicData.builder()
                .cityBasicInfo(cityData)
                .cityRankingData(cityRankingData)
                .build();
    }

    public CityRankingData getWorkRankingData(String provinceCode, String cityCode) {
        return cityRankingRepository.findByProvinceCodeAndCityCodeAndYear(provinceCode, cityCode, "2022");

    }
    /**
     * 지역 기초 정보
     * 인구 수, 가구 수, 인구 밀도, 노령화지수, 평균 연령 정보
     * */
    private CityBasicInfo getCityData(Query query) {
        query.fields().exclude("employCnt").exclude("corpCnt");
        return mongoTemplate.findOne(query, CityBasicInfo.class, "city_basic");
    }

    /**
     * 시/도 내 모든 군/구 인구 수 정보
     * */
    private CityRankingData getCityRankingData(Query query) {
        return mongoTemplate.findOne(query, CityRankingData.class, "city_ranking");
    }

    /**
     * 지역 직장/종사자 수 조회
     * */
    public List<Employment> getWorkData(String provinceCode, String cityCode) {
        Query query = new Query();
        if (provinceCode != null) {
            query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        }
        if (cityCode != null) {
            query.addCriteria(Criteria.where("cityCode").is(cityCode));
        }

        query.addCriteria(Criteria.where("year").is("2022"));
        return mongoTemplate.find(query, Employment.class, "employment");
    }


    /**
     * 시/도 내 모든 군/구 인구 수 조회
     * */
    public List<CityBasicInfo> getCityPopInPvc(String provinceCode, String year) {
        Query query = new Query();
        Map<String, String> cityCodes = getCityCodes(provinceCode);

        List<String> admCdList = new ArrayList<>(cityCodes.keySet());

        query.fields().include("totalPopulation")
                .include("provinceCode")
                .include("cityCode")
                .include("admCd");

        query.addCriteria(Criteria.where("admCd").in(admCdList));
        query.addCriteria(Criteria.where("year").is(year));
        List<CityBasicInfo> cityBasic = mongoTemplate.find(query, CityBasicInfo.class, "city_basic");
        cityBasic.forEach(info -> {
            String cityName = cityCodes.get(info.getAdmCd());
            info.setCityName(cityName);
        });

        return cityBasic;
    }

    /**
     * 동일 시/도 내 모든 군/구 직장/종사자 수 조회
     * */
    public List<Employment> getWorkDataInPvc(String provinceCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        query.addCriteria(
                new Criteria().orOperator(
                        Criteria.where("townCode").is(null),
                        Criteria.where("townCode").is("")
                )
        );
        query.addCriteria(Criteria.where("year").is("2022"));
        return mongoTemplate.find(query, Employment.class, "employment");
    }

    public CityPopulationData getPopulationDataInCity(String provinceCode, String cityCode) {
        //인구 정보는 연도 조건 없음
        Query common = new Query();

        if (provinceCode != null) {
            common.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        }
        if (cityCode != null) {
            common.addCriteria(Criteria.where("cityCode").is(cityCode));
        }

        List<CityPopulation> population = getPopulation(common);
        List<PopRankingData> populationRank = getPopulationRank(common);

        return CityPopulationData.builder()
                .cityPopulation(population)
                .popRankingData(populationRank)
                .build();

    }

    private List<CityPopulation> getPopulation(Query query) {
        return mongoTemplate.find(query, CityPopulation.class, "population");
    }
    /**
     *
     * */
    private List<PopRankingData> getPopulationRank(Query query) {
        return mongoTemplate.find(query, PopRankingData.class, "pop_ranking");
    }

    /**
     * 모든 지역 코드 리스트 조회
     * */
    public Map<String, String> getCityCodes(String provinceId) {
        Query query = new Query(Criteria.where("_id").is(provinceId));
        District district = mongoTemplate.findOne(query, District.class, "district");

        if (district == null || district.getCities() == null) {
            return new HashMap<>(); // 결과가 없으면 빈 맵 반환
        }

        return district.getCities().stream()
                .collect(Collectors.toMap(
                        city -> provinceId + city.getCityCode(),  // key: admCd
                        city -> city.getCityName()                // value: admNm
                ));
    }

}
