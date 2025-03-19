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
            ProvinceDto district = getDistrict(code);

            messageSenderService.publishDistrictMessage(district);
        }
    }
    private ProvinceDto getDistrict(String provinceCode) {
        List<AdministrativeDistrict> districts = districtRepository.findByProvinceCode(provinceCode);

        Map<String, CityDto> cityMap = new LinkedHashMap<>();

        for (AdministrativeDistrict district : districts) {
            String cityKey = district.getCityDistrictCode();

            cityMap.putIfAbsent(cityKey, new CityDto());
            CityDto city = cityMap.get(cityKey);
            city.setCityCode(district.getCityDistrictCode());
            city.setCityName(district.getCityDistrictName());

            if (city.getTowns() == null) {
                city.setTowns(new ArrayList<>());
            }

            TownDto townDto = new TownDto();
            townDto.setTownName(district.getTownName());
            townDto.setTownCode(district.getTownCode());
            city.getTowns().add(townDto);
        }

        ProvinceDto province = new ProvinceDto();
        province.set_id(provinceCode);
        province.setProvinceName(districts.isEmpty() ? "" : districts.get(0).getProvinceName());
        province.setCities(new ArrayList<>(cityMap.values()));

        return province;
    }



}
