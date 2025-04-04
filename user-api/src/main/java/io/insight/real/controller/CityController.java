package io.insight.real.controller;

import io.insight.real.dto.CityEmploymentData;
import io.insight.real.dto.CityInfoData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.dto.response.ResponseData;
import io.insight.real.service.in.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "지역 기초 정보 조회", description = "지역 기초 정보를 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(        schema = @Schema(implementation = CityInfoData.class)))
    @GetMapping("/province/{province}/city/{city}")

    public ResponseEntity<?> getCityRankInfo(@RequestParam("province") String provinceCode,
                                                           @RequestParam("city") String cityCode,
                                                           @RequestParam("year") String year) {
        CityInfoData cityInfoData = cityService.getCityData(provinceCode, cityCode, year);
        return ResponseData.success(cityInfoData);
    }


    @Operation(summary = "지역 직장/종사자 정보 조회", description = "지역 직장/종사자 정보를 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityEmploymentData.class))))
    @GetMapping("/province/{province}/city/{city}/work")
    public ResponseEntity<?> getCityWorkInfo(@PathVariable("province") String provinceCode,
                                             @PathVariable("city") String cityCode
                                             ) {
        List<CityEmploymentData> workData = cityService.getCityWorkData(provinceCode, cityCode);
        return ResponseData.success(workData);
    }

    @Operation(summary = "선택 도시 내 모든 군/구의 직장/종사자 수 조회", description = "지역 직장/종사자 정보를 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityEmploymentData.class))))
    @GetMapping("/province/{province}/work")
    public ResponseEntity<?> getCityWorkInfo(@PathVariable("province") String provinceCode
    ) {
        List<CityEmploymentData> workData = cityService.getWorkDataInPvc(provinceCode);
        return ResponseData.success(workData);
    }

    @Operation(summary = "도시 직장/종사자 수 순위 정보 조회", description = "지역 직장/종사자 순위 정보를 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(        schema = @Schema(implementation = CityRankingData.class)))
    @GetMapping("/province/{province}/city/{city}/ranking/work")
    public ResponseEntity<?> getWorkRankInfo(@PathVariable("province") String provinceCode,
                                             @PathVariable("city") String cityCode
    ) {
        CityRankingData workRankingData = cityService.getWorkRankingData(provinceCode, cityCode);
        return ResponseData.success(workRankingData);
    }
}
