package io.insight.real.controller;

import io.insight.real.dto.response.ResponseData;
import io.insight.real.infra.repository.entity.District;
import io.insight.real.service.in.DistrictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class DistrictController {
    private final DistrictService districtService;

    @Operation(summary = "도시 지역 리스트 조회", description = "전국 도시 코드를 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = District.class))))
    @GetMapping("/districts")
    public ResponseEntity<?> getDistrictInfo() {
        return ResponseData.success(districtService.getDistricts());
    }


}
