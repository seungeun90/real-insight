package io.insight.real.infra.repository;

import io.insight.real.dto.CityPopulationData;
import io.insight.real.dto.PopRankingData;
import io.insight.real.infra.repository.entity.CityPopulation;
import io.insight.real.infra.repository.entity.District;
import io.insight.real.service.out.PopulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class PopulationRepositoryImpl implements PopulationRepository {
    private final MongoTemplate mongoTemplate;

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
