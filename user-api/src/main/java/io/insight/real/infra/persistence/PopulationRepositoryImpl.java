package io.insight.real.infra.persistence;

import io.insight.real.dto.PopRankingData;
import io.insight.real.dto.PopulationData;
import io.insight.real.infra.persistence.entity.CityPopulation;
import io.insight.real.infra.persistence.entity.District;
import io.insight.real.infra.persistence.mapper.PopulationMapper;
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
    private final PopulationMapper populationMapper;

    public List<PopulationData> getPopulation(String provinceCode, String cityCode) {
        Query query = new Query();

        if (provinceCode != null) {
            query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        }
        if (cityCode != null) {
            query.addCriteria(Criteria.where("cityCode").is(cityCode));
        }

        query.addCriteria(
                new Criteria().andOperator(
                        Criteria.where("townCode").ne(null),
                        Criteria.where("townCode").ne("")
                )
        );

        List<CityPopulation> population = mongoTemplate.find(query, CityPopulation.class, "population");
        return populationMapper.toDtoList(population);
    }
    /**
     *
     * */
    public List<PopRankingData> getPopulationRank(String provinceCode, String cityCode) {
        Query query = new Query();

        if (provinceCode != null) {
            query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        }
        if (cityCode != null) {
            query.addCriteria(Criteria.where("cityCode").is(cityCode));
        }
        query.addCriteria(
                new Criteria().andOperator(
                        Criteria.where("townCode").ne(null),
                        Criteria.where("townCode").ne("")
                )
        );

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
