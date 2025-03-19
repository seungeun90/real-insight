package io.insight.real.city.service;

import io.insight.real.city.dto.CityDto;
import io.insight.real.city.dto.ProvinceDto;
import io.insight.real.city.dto.TownDto;
import io.insight.real.city.repository.entity.AdministrativeDistrict;
import io.insight.real.city.repository.jpa.AdministrativeDistrictRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegionUpdateService {
    private final AdministrativeDistrictRepository districtRepository;
    private final MessageSenderService messageSenderService;

    public void publishDistrictMessage(){
        List<String> distinctProvinceCodes = districtRepository.findDistinctProvinceCodes();
        for (String code : distinctProvinceCodes) {
            Map<String, Object> district = getDistrict(code);
            messageSenderService.publishDistrictMessage(district);
        }
    }
    /*private ProvinceDto getDistrict(String provinceCode) {
        List<AdministrativeDistrict> districts = districtRepository.findByProvinceCode(provinceCode);

        Map<String, CityDto> cityMap = new LinkedHashMap<>();

        for (AdministrativeDistrict district : districts) {
            String cityKey = district.getCityDistrictCode();

            cityMap.putIfAbsent(cityKey, new CityDto());
            CityDto city = cityMap.get(cityKey);
            city.setCityCode(district.getCityDistrictCode());
            city.setCityName(district.getCityDistrictName());
            log.info("getCity :name{}, code{} " ,district.getCityDistrictCode(), district.getCityDistrictName());
            if (city.getTowns() == null) {
                city.setTowns(new ArrayList<>());
            }

            TownDto townDto = new TownDto();
            townDto.setTownName(district.getTownName());
            townDto.setTownCode(district.getTownCode());
            log.info("City Town:name{}, code{} " ,townDto.getTownName(), townDto.getTownCode());
            city.getTowns().add(townDto);
        }

        ProvinceDto province = new ProvinceDto();
        province.set_id(provinceCode);
        province.setProvinceName(districts.isEmpty() ? "" : districts.get(0).getProvinceName());
        province.setCities(new ArrayList<>(cityMap.values()));

        return province;
    }*/
    private Map<String, Object> getDistrict(String provinceCode) {
        List<AdministrativeDistrict> districts = districtRepository.findByProvinceCode(provinceCode);
        // 도시별로 그룹화
        Map<String, Map<String, Object>> cityMap = new LinkedHashMap<>();

        for (AdministrativeDistrict district : districts) {
            String cityKey = district.getCityDistrictCode();

            // 도시 정보 생성
            cityMap.putIfAbsent(cityKey, new HashMap<>());
            cityMap.get(cityKey).put("cityCode", district.getCityDistrictCode());
            cityMap.get(cityKey).put("cityName", district.getCityDistrictName());

            // towns 배열 추가
            cityMap.get(cityKey)
                    .computeIfAbsent("towns", k -> new ArrayList<Map<String, String>>());

            // Map으로 town 추가 (이름:코드)
            Map<String, String> townMap = new HashMap<>();
            townMap.put(district.getTownName(), district.getTownCode());
            ((List<Map<String, String>>) cityMap.get(cityKey).get("towns")).add(townMap);
        }

        // 최종 JSON 구조 생성
        Map<String, Object> provinceJson = new HashMap<>();
        provinceJson.put("_id", provinceCode);
        provinceJson.put("provinceName", districts.isEmpty() ? "" : districts.get(0).getProvinceName());
        provinceJson.put("cities", new ArrayList<>(cityMap.values()));

        return provinceJson;
    }



}
