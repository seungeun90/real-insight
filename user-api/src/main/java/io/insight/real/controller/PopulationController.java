package io.insight.real.controller;

import io.insight.real.dto.CityPopulationData;
import io.insight.real.service.in.PopulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class PopulationController {
    private final PopulationService populationService;
    /**
     * 지역 인구 비율, 랭킹 정보
     * */
    @GetMapping("/city/pop/province/{province}/city/{city}")
    public ResponseEntity<?> getCityPopInfo(@PathVariable("province") String provinceCode,
                                            @PathVariable("city") String cityCode) {
        CityPopulationData populationDataInCity = populationService.getPopulationDataInCity(provinceCode, cityCode);
        return new ResponseEntity<>(populationDataInCity, HttpStatus.OK);
    }
}
