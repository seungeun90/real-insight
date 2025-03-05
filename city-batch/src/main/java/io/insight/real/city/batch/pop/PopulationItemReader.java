package io.insight.real.city.batch.pop;

import io.insight.real.city.dto.request.CityBasicInfoRequest;
import io.insight.real.city.repository.AdmDistrictSearchRepositoryImpl;
import io.insight.real.city.repository.jpa.AdministrativeDistrictRepository;
import io.insight.real.city.repository.jpa.PopulationRepository;
import io.insight.real.city.repository.entity.AdministrativeDistrict;
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
public class PopulationItemReader implements ItemReader<List<CityBasicInfoRequest>> {

    private AdmDistrictSearchRepositoryImpl admDistrictSearchRepositoryImpl;
    private PopulationRepository populationRepository;
    private Set<String> processedCodes = new HashSet<>();
    private final List<AdministrativeDistrict> admDistricts;
    private int index = 0; // 현재 읽고 있는 위치 인덱스
    public PopulationItemReader(AdministrativeDistrictRepository admDistrictRepository,
                                AdmDistrictSearchRepositoryImpl admDistrictSearchRepositoryImpl,
                                PopulationRepository populationRepository,
                                @Value("#{jobParameters['code']}") String code) {
        this.admDistrictSearchRepositoryImpl = admDistrictSearchRepositoryImpl;
        this.populationRepository = populationRepository;
        // JobParameter(code)가 있으면 특정 코드만 조회, 없으면 전체 조회
        this.admDistricts = (code != null) ?
                admDistrictRepository.findByProvinceCode(code) :
                admDistrictRepository.findAll();
    }

    @Override
    public List<CityBasicInfoRequest> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        List<CityBasicInfoRequest> requests = new ArrayList<>();

        if (index >= admDistricts.size()) {
            return null; // 모든 데이터를 읽으면 배치 종료
        }
        AdministrativeDistrict province = admDistricts.get(index++); // 한 개씩 반환

        String provinceCode = province.getProvinceCode();
        boolean hasProvinceCode = populationRepository.existsByAdmCd(provinceCode);

        String lastCityCode = "";
        boolean hasCityCode = false;
        List<AdministrativeDistrict> missingRecords = admDistrictSearchRepositoryImpl.findMissingRecords(provinceCode);
        for (AdministrativeDistrict district : missingRecords) {

            StringBuilder strBuilder = new StringBuilder(provinceCode);
            String cityDistrictCode = strBuilder.append(district.getCityDistrictCode()).toString();
            String townCode = strBuilder.append(district.getTownCode()).toString();

            if(!lastCityCode.equals(cityDistrictCode)) {
                lastCityCode = cityDistrictCode;
                hasCityCode = populationRepository.existsByAdmCd(cityDistrictCode);
            }

            if (!hasProvinceCode && processedCodes.add(provinceCode)) {
                requests.add(new CityBasicInfoRequest(provinceCode));
            }

            if (!hasCityCode && processedCodes.add(cityDistrictCode)) {
                requests.add(new CityBasicInfoRequest(cityDistrictCode));
            }

            if (processedCodes.add(townCode)) {
                requests.add(new CityBasicInfoRequest(townCode));
            }
        }
        return requests;
    }


}
