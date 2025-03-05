package io.insight.real.controller;

import io.insight.real.infra.repository.entity.District;
import io.insight.real.service.in.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class DistrictController {
    private final DistrictService districtService;

    /**
     * 모든 도시 지역 리스트
     * 시/도, 군/구, 동/읍/면
     * */
    @GetMapping("/districts")
    public ResponseEntity<?> getDistrictInfo() {
        List<District> districts = districtService.getDistricts();
        return new ResponseEntity<>(districts, HttpStatus.OK);
    }

}
