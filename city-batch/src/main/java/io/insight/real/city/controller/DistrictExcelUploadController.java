package io.insight.real.city.controller;

import io.insight.real.city.service.ExcelReaderService;
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

    private final ExcelReaderService excelReaderService;

    @PostMapping("/district")
    public ResponseEntity<?> uploadDistrictData(@RequestParam("file") MultipartFile file){
        excelReaderService.readAndInsertExcel(file);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/districts/count")
    public ResponseEntity<?> getDistrictData(){
        long districtDataCount = excelReaderService.getDistrictDataCount();
        return ResponseEntity.status(HttpStatus.OK).body(districtDataCount);
    }


}
