package io.insight.real.infra.persistence;

import io.insight.real.dto.CityBasicInfoData;
import io.insight.real.dto.CityInfoData;
import io.insight.real.dto.CityEmploymentData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.persistence.entity.CityRanking;
import io.insight.real.infra.persistence.entity.CityBasicInfo;
import io.insight.real.infra.persistence.entity.District;
import io.insight.real.infra.persistence.entity.Employment;
import io.insight.real.infra.persistence.jpa.CityRankingRepository;
import io.insight.real.infra.persistence.mapper.CityInfoMapper;
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
    private final CityInfoMapper cityInfoMapper;
    /**
     * 지역 기초 정보 조회
     * */
    @Override
    public CityInfoData findCityData(String provinceCode, String cityCode, String year) {

        CityBasicInfoData cityData = getCityData(provinceCode+cityCode,year);
        CityRankingData cityRanking = getCityRankingData(provinceCode, cityCode, year);

        CityRanking workRank = cityRankingRepository.findByProvinceCodeAndCityCodeAndYear(provinceCode, cityCode, "2022");
        if(workRank != null) {
            cityRanking.setEmployCntRank(workRank.getEmployCntRank());
            cityRanking.setCorpCntRank(workRank.getCorpCntRank());
        }

        return CityInfoData.builder()
                .cityBasicInfo(cityData)
                .cityRanking(cityRanking)
                .build();
    }

    public CityRankingData getWorkRankingData(String provinceCode, String cityCode) {
        CityRanking cityRanking = cityRankingRepository.findByProvinceCodeAndCityCodeAndYear(provinceCode, cityCode, "2022");
        return cityInfoMapper.toRankingDto(cityRanking);


    }
    /**
     * 지역 기초 정보
     * 인구 수, 가구 수, 인구 밀도, 노령화지수, 평균 연령 정보
     * */
    public CityBasicInfoData getCityData(String admCd, String year) {
        Query query = new Query();
        query.addCriteria(Criteria.where("admCd").is(admCd));
        query.addCriteria(Criteria.where("year").is(year));
        query.fields().exclude("employCnt").exclude("corpCnt");
        CityBasicInfo cityBasic = mongoTemplate.findOne(query, CityBasicInfo.class, "city_basic");
        return cityInfoMapper.toCityDto(cityBasic);
    }

    /**
     * 시/도 내 모든 군/구 인구 수 정보
     * */
    private CityRankingData getCityRankingData(String provinceCode, String cityCode, String year) {
        Query query = new Query();
        if (provinceCode != null) {
            query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        }
        if (cityCode != null) {
            query.addCriteria(Criteria.where("cityCode").is(cityCode));
        }
        if (year != null) {
            query.addCriteria(Criteria.where("year").is(year));
        }

        CityRanking cityRanking = mongoTemplate.findOne(query, CityRanking.class, "city_ranking");
        return cityInfoMapper.toRankingDto(cityRanking);
    }

    /**
     * 지역 직장/종사자 수 조회
     * */
    public List<CityEmploymentData> getWorkData(String provinceCode, String cityCode) {
        Query query = new Query();
        if (provinceCode != null) {
            query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        }
        if (cityCode != null) {
            query.addCriteria(Criteria.where("cityCode").is(cityCode));
        }

        query.addCriteria(Criteria.where("year").is("2022"));
        List<Employment> employment = mongoTemplate.find(query, Employment.class, "employment");
        return cityInfoMapper.toEmpDtoList(employment);
    }


    /**
     * 시/도 내 모든 군/구 인구 수 조회
     * */
    public List<CityBasicInfoData> getCityPopInPvc(String provinceCode, String year) {
        Query query = new Query();
        Map<String, String> cityCodes = getCityCodes(provinceCode);

        List<String> admCdList = new ArrayList<>(cityCodes.keySet());

        query.fields().include("totalPopulation") //인구 수
                .include("householdCount") //가구 수
                .include("averageHouseholdSize")// 평균 가구원 수
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

        return cityInfoMapper.toCityDtoList(cityBasic);
    }

    /**
     * 동일 시/도 내 모든 군/구 직장/종사자 수 조회
     * */
    public List<CityEmploymentData> getWorkDataInPvc(String provinceCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        query.addCriteria(
                new Criteria().orOperator(
                        Criteria.where("townCode").is(null),
                        Criteria.where("townCode").is("")
                )
        );
        query.addCriteria(Criteria.where("year").is("2022"));
        List<Employment> employment = mongoTemplate.find(query, Employment.class, "employment");
        return cityInfoMapper.toEmpDtoList(employment);
    }

    public List<CityBasicInfoData> getTownInfoInCity(String provinceCode, String cityCode) {
        Query query = new Query();
        //Map<String, String> cityCodes = getCityCodes(provinceCode);
        //List<String> admCdList = new ArrayList<>(cityCodes.keySet());

        query.fields().include("totalPopulation")
                .include("householdCount")
                .include("averageHouseholdSize")
                .include("provinceCode")
                .include("cityCode")
                .include("admCd");

        query.addCriteria(Criteria.where("provinceCode").is(provinceCode));
        query.addCriteria(Criteria.where("cityCode").is(cityCode));
        //Criteria.where("townCode").is("");
       // query.addCriteria(Criteria.where("admcd").regex("^" + provinceCode + cityCode));
        query.addCriteria(Criteria.where("year").is("2023"));
        List<CityBasicInfo> cityBasic = mongoTemplate.find(query, CityBasicInfo.class, "city_basic");

        return cityInfoMapper.toCityDtoList(cityBasic);
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
