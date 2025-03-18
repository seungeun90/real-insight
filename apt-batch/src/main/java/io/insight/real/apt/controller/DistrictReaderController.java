package io.insight.real.apt.controller;

import io.insight.real.apt.repository.entity.Region;
import io.insight.real.apt.service.LegalDistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/districts/count")
    public ResponseEntity<?> getDistrictDataCount(){
        long count = legalDistrictService.getDistrictsCount();
        return ResponseEntity.status(HttpStatus.OK).body(count);
    }

    @GetMapping("/regions")
    public ResponseEntity<?> getDistrictData(){
        List<Region> regions = legalDistrictService.getRegions();
        return ResponseEntity.status(HttpStatus.OK).body(regions);
    }

}
