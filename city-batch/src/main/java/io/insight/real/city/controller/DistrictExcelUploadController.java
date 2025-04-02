package io.insight.real.city.controller;

import io.insight.real.city.service.in.DistrictService;
import io.insight.real.city.service.in.RegionMessageSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
public class DistrictExcelUploadController {

    private final DistrictService districtService;
    private final RegionMessageSenderService regionMessageSenderService;

    @PostMapping("/district")
    public ResponseEntity<?> uploadDistrictData(@RequestParam("file") MultipartFile file){
        districtService.readExcelAndSave(file);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/districts/count")
    public ResponseEntity<?> getDistrictData(){
        long districtDataCount = districtService.getDistrictDataCount();
        return ResponseEntity.status(HttpStatus.OK).body(districtDataCount);
    }

    @GetMapping("/districts/temp")
    public ResponseEntity<?> sendDistrictDataToUserSystem(){
        regionMessageSenderService.publishDistrictMessage();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
