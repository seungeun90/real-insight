package io.insight.real.apt.controller;

import io.insight.real.apt.dto.response.ResponseData;
import io.insight.real.apt.repository.jpa.entity.RegionJpa;
import io.insight.real.apt.service.in.LegalDistrictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DistrictReaderController {

    private final LegalDistrictService legalDistrictService;

    @PostMapping("/district")
    public ResponseEntity<?> uploadDistrictData(@RequestParam("file") MultipartFile file){
        legalDistrictService.readLegalDistricts(file);
        return ResponseData.success(null);
    }

    @GetMapping("/districts/count")
    public ResponseEntity<?> getDistrictDataCount(){
        long count = legalDistrictService.getDistrictsCount();
        return ResponseData.success(count);
    }

    @Operation(summary = "지역 목록 조회", description = "지역 목록 정보를 반환한다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegionJpa.class))))
    @GetMapping("/regions")
    public ResponseEntity<?> getDistrictData(){
        List<RegionJpa> regionJpas = legalDistrictService.getRegions();
        return ResponseData.success(regionJpas);
    }

}
