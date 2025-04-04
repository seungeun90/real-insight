package io.insight.real.controller;

import io.insight.real.dto.CityPopulationData;
import io.insight.real.dto.response.ResponseData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.service.in.PopulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
public class PopulationController {
    private final PopulationService populationService;


    @Operation(summary = "선택 도시 내 모든 군/구의 인구 수 조회", description = "선택 도시 내 모든 군/구의 인구 수를 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CityBasicInfo.class))))
    @GetMapping("/province/{province}/population")
    public ResponseEntity<?> getCityBasicInfo(@RequestParam("provinceCode") String provinceCode,
                                              @RequestParam("year") String year) {
        List<CityBasicInfo> popDataInPvc = populationService.getPopDataInPvc(provinceCode, year);
        return ResponseData.success(popDataInPvc);
    }

    @Operation(summary = "지역 인구 비율, 랭킹 정보 조회", description = "지역 인구 비율, 랭킹 정보를 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(        schema = @Schema(implementation = CityPopulationData.class)))
    @GetMapping("/province/{province}/city/{city}/population")
    public ResponseEntity<?> getCityPopInfo(@PathVariable("province") String provinceCode,
                                            @PathVariable("city") String cityCode) {
        CityPopulationData populationDataInCity = populationService.getPopulationDataInCity(provinceCode, cityCode);
        return ResponseData.success(populationDataInCity);
    }
}
