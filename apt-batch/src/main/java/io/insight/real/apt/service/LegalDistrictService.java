package io.insight.real.apt.service;

import io.insight.real.apt.repository.jpa.entity.Province;
import io.insight.real.apt.repository.jpa.entity.Region;
import io.insight.real.apt.repository.jpa.DistrictRepository;
import io.insight.real.apt.repository.jpa.entity.LegalDistrict;
import io.insight.real.apt.repository.jpa.ProvinceRepository;
import io.insight.real.apt.repository.jpa.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class LegalDistrictService {
    private final DistrictRepository districtRepository;
    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;

    public List<Region> getRegions() {
        return regionRepository.findAllByStatus("존재");
    }

    @Transactional
    public void readLegalDistricts(MultipartFile file) {
        List<LegalDistrict> districtList = new ArrayList<>();
        Map<String, Long> idMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(),"EUC-KR"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");

                if (fields.length < 3) continue;  // 데이터 형식이 올바르지 않으면 건너뛴다.
                //코드
                String districtCode = fields[0].trim();
                String regionCode = districtCode.length() >= 5 ? districtCode.substring(0, 5) : null;
                String name = fields[1].trim();
                String status = fields[2].trim();

                String[] parts = name.split(" ");
                if(parts.length == 1) {
                    Province province = Province.builder().code(regionCode).name(name).status(status).build();
                    provinceRepository.save(province);
                } else if (parts.length == 2) {
                    if(idMap.get(regionCode)==null) {
                        Region region = Region.builder().code(regionCode).name(name).status(status).build();
                        Region saved = regionRepository.save(region);
                        idMap.put(regionCode, saved.getId());
                    }
                } else {
                    Long regionId = idMap.get(regionCode);
                    LegalDistrict district = LegalDistrict.builder()
                            .regionId(regionId)
                            .code(districtCode)
                            .name(name)
                            .status(status)
                            .build();
                    districtList.add(district);
                }
            }
            districtRepository.saveAll(districtList);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public long getDistrictsCount() {
        return districtRepository.count();
    }
}
