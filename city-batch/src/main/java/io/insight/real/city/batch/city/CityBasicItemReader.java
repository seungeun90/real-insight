package io.insight.real.city.batch.city;

import io.insight.real.city.dto.request.CityBasicInfoRequest;
import io.insight.real.city.repository.entity.AdministrativeDistrict;
import io.insight.real.city.repository.entity.CityBasicInfo;
import io.insight.real.city.repository.jpa.AdministrativeDistrictRepository;
import io.insight.real.city.repository.jpa.CityBasicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@StepScope
public class CityBasicItemReader implements ItemReader<List<CityBasicInfoRequest>> {

    private final AdministrativeDistrictRepository districtRepository;
    private final CityBasicRepository cityRepository;

    private final List<AdministrativeDistrict> admDistricts;
    private int index = 0; // 현재 읽고 있는 위치 인덱스
    private Set<String> processedProvinceCodes = new HashSet<>();
    private Set<String> processedDistrictCodes = new HashSet<>();

    public CityBasicItemReader(AdministrativeDistrictRepository admDistrictRepository,
                               AdministrativeDistrictRepository districtRepository,
                               CityBasicRepository cityRepository,
                               @Value("#{jobParameters['code']}") String code) {
        this.districtRepository = districtRepository;
        this.cityRepository = cityRepository;
        // JobParameter(code)가 있으면 특정 코드만 조회, 없으면 전체 조회
        this.admDistricts = (code != null) ?
                admDistrictRepository.findByProvinceCode(code) :
                admDistrictRepository.findAll();
    }
    public List<CityBasicInfoRequest> read() {

        List<CityBasicInfoRequest> requests = new ArrayList<>();

        if (index >= admDistricts.size()) {
            return null; // 모든 데이터를 읽으면 배치 종료
        }

        AdministrativeDistrict province = admDistricts.get(index++); // 한 개씩 반환

        String provinceCode = province.getProvinceCode();
        List<AdministrativeDistrict> districts = districtRepository.findByProvinceCode(provinceCode);

        for (AdministrativeDistrict district : districts) {

            StringBuilder strBuilder = new StringBuilder(province.getProvinceCode());
            String cityDistrictCode = strBuilder.append(district.getCityDistrictCode()).toString();
            String townCode = strBuilder.append(district.getTownCode()).toString();

            List<String> codes = new ArrayList<>();

            // 중복된 provinceCode 추가 방지
            if (processedProvinceCodes.add(provinceCode)) {
                codes.add(provinceCode);
            }
            // 중복된 cityDistrictCode 추가 방지
            if (processedDistrictCodes.add(cityDistrictCode)) {
                codes.add(cityDistrictCode);
            }
            if (processedDistrictCodes.add(townCode)) {
                codes.add(townCode);
            }
            processUpdateTargetCode(codes, requests);
        }

        return requests;
    }

    public void processUpdateTargetCode(List<String> codes, List<CityBasicInfoRequest> requests){
        for (String code : codes) {
            List<CityBasicInfo> infos = cityRepository.findByAdmCd(code);
            if(infos.isEmpty()) {
                createRequests(requests, code);
            } else {
                //존재하지 않는 해만 조회
                checkMissingData(requests, infos);
            }
        }
    }

    public void checkMissingData(List<CityBasicInfoRequest> requests, List<CityBasicInfo> updateList ) {
        Set<String> allYears = IntStream.rangeClosed(2000, 2023)
                .mapToObj(String::valueOf)
                .collect(Collectors.toSet());

        Set<String> existingYears = updateList.stream()
                .map(CityBasicInfo::getYear)
                .collect(Collectors.toSet());

        // 각 UpdatedCity 의 admCd를 유지하면서 누락된 연도만 추가
        for (CityBasicInfo uc : updateList) {
            for (String year : allYears) {
                if (!existingYears.contains(year)) { // 실제 없는 연도만 추가
                    requests.add(new CityBasicInfoRequest(uc.getAdmCd(), year));
                }
            }
        }

    }


    public void createRequests(List<CityBasicInfoRequest> requests, String admCd) {
        Set<String> allYears = IntStream.rangeClosed(2000, 2023)
                .mapToObj(String::valueOf)
                .collect(Collectors.toSet());
        allYears.forEach(y -> requests.add(new CityBasicInfoRequest(admCd, y)));
    }


}
