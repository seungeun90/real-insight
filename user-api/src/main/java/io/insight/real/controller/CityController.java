package io.insight.real.controller;

import io.insight.real.dto.CityBasicData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.Employment;
import io.insight.real.service.in.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CityController {

    private final CityService cityService;

    /**
     * 지역 기초 정보
     * */
    @GetMapping("/city")
    public ResponseEntity<?> getCityRankInfo(@RequestParam("provinceCode") String provinceCode,
                                                           @RequestParam("cityCode") String cityCode,
                                                           @RequestParam("year") String year) {
        CityBasicData cityBasicData = cityService.getCityData(provinceCode, cityCode, year);
        return new ResponseEntity<>(cityBasicData, HttpStatus.OK);
    }


    /**
     * 선택 시/도 내 모든 군/구의 인구 수
     * */
    @GetMapping("/cities/pop")
    public ResponseEntity<?> getCityBasicInfo(@RequestParam("provinceCode") String provinceCode,
                                              @RequestParam("year") String year) {
        List<CityBasicInfo> popDataInPvc = cityService.getPopDataInPvc(provinceCode, year);
        return new ResponseEntity<>(popDataInPvc, HttpStatus.OK);
    }

    /**
     * 지역 직장/종사자 정보
     * */
    @GetMapping("/work/province/{province}/city/{city}")
    public ResponseEntity<?> getCityWorkInfo(@PathVariable("province") String provinceCode,
                                             @PathVariable("city") String cityCode
                                             ) {
        List<Employment> workData = cityService.getCityWorkData(provinceCode, cityCode);
        return new ResponseEntity<>(workData, HttpStatus.OK);
    }
    /**
     * 선택 시/도 내 모든 군/구의 직장/종사자 수
     * */
    @GetMapping("/work/province/{province}")
    public ResponseEntity<?> getCityWorkInfo(@PathVariable("province") String provinceCode
    ) {
        List<Employment> workData = cityService.getWorkDataInPvc(provinceCode);
        return new ResponseEntity<>(workData, HttpStatus.OK);
    }
    @GetMapping("/work/rank/province/{province}/city/{city}")
    public ResponseEntity<?> getWorkRankInfo(@PathVariable("province") String provinceCode,
                                             @PathVariable("city") String cityCode
    ) {
        CityRankingData workRankingData = cityService.getWorkRankingData(provinceCode, cityCode);
        return new ResponseEntity<>(workRankingData, HttpStatus.OK);
    }
}
