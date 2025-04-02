package io.insight.real.city.service;

import io.insight.real.city.repository.entity.AdministrativeDistrict;
import io.insight.real.city.service.in.DistrictService;
import io.insight.real.city.service.in.RegionMessageSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegionMessageSenderServiceImpl implements RegionMessageSenderService {
    private final DistrictService districtService;
    private final MessageSenderService messageSenderService;

    @Override
    public void publishDistrictMessage(){
        List<String> distinctProvinceCodes = districtService.getDistinctProvinceCodes();
        for (String code : distinctProvinceCodes) {
            Map<String, Object> district = getDistrict(code);
            messageSenderService.publishDistrictMessage(district);
        }
    }
    private Map<String, Object> getDistrict(String provinceCode) {
        List<AdministrativeDistrict> districts = districtService.getByProvinceCode(provinceCode);
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
