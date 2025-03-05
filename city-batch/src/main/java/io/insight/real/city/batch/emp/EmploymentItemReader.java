package io.insight.real.city.batch.emp;

import io.insight.real.city.dto.request.CityBasicInfoRequest;
import io.insight.real.city.repository.entity.AdministrativeDistrict;
import io.insight.real.city.repository.jpa.AdministrativeDistrictRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@StepScope
public class EmploymentItemReader implements ItemReader<List<CityBasicInfoRequest>> {
    private final AdministrativeDistrictRepository districtRepository;
    private Set<String> processedCodes = new HashSet<>();
    private final List<AdministrativeDistrict> admDistricts;
    private int index = 0; // 현재 읽고 있는 위치 인덱스
    private final String year ;
    public EmploymentItemReader(AdministrativeDistrictRepository admDistrictRepository,
                                AdministrativeDistrictRepository districtRepository,
                                @Value("#{jobParameters['code']}") String code,
                                @Value("#{jobParameters['year']}") String year) {
        this.districtRepository = districtRepository;
        // JobParameter(code)가 있으면 특정 코드만 조회, 없으면 전체 조회
        this.admDistricts = (code != null) ?
                admDistrictRepository.findByProvinceCode(code) :
                admDistrictRepository.findAll();

        this.year = year;
    }

    @Override
    public List<CityBasicInfoRequest> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        List<CityBasicInfoRequest> requests = new ArrayList<>();

        if (index >= admDistricts.size()) {
            return null; // 모든 데이터를 읽으면 배치 종료
        }
        AdministrativeDistrict province = admDistricts.get(index++); // 한 개씩 반환

        String provinceCode = province.getProvinceCode();

        List<AdministrativeDistrict> districts = districtRepository.findByProvinceCode(provinceCode);

        for (AdministrativeDistrict district : districts) {
            String cityCode = provinceCode + district.getCityDistrictCode();

            if (processedCodes.add(provinceCode)) {
                requests.add(new CityBasicInfoRequest(provinceCode, year));
            }

            if (processedCodes.add(cityCode)) {
                requests.add(new CityBasicInfoRequest(cityCode, year));
            }

        }
        if(requests.isEmpty()) {
            return null;
        }
        return requests;
    }


}
