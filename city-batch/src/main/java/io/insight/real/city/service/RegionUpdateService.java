package io.insight.real.city.service;

import io.insight.real.city.repository.entity.AdministrativeDistrict;
import io.insight.real.city.repository.jpa.AdministrativeDistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class RegionUpdateService {
    private final AdministrativeDistrictRepository districtRepository;

    public Map<String, Object>  getDistrict(String provinceCode) {
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
                    .computeIfAbsent("towns", k -> new ArrayList<String>());
            ((List<String>) cityMap.get(cityKey).get("towns")).add(district.getTownName());
        }

        // 최종 JSON 구조 생성
        Map<String, Object> provinceJson = new HashMap<>();
        provinceJson.put("_id", provinceCode);
        provinceJson.put("provinceName", districts.isEmpty() ? "" : districts.get(0).getProvinceName());
        provinceJson.put("cities", new ArrayList<>(cityMap.values()));

        return provinceJson;
    }

}
