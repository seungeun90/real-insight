package io.insight.real.city.controller;

import io.insight.real.city.dto.JobName;
import io.insight.real.city.dto.request.BatchRunRequest;
import io.insight.real.city.service.in.JobTriggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/batch")
@RestController
public class BatchTriggerController {

    private final JobTriggerService jobTriggerService;

    @PostMapping("/population")
    public ResponseEntity<String> runPopBatch(@RequestBody BatchRunRequest request) {
        try {
            jobTriggerService.run(JobName.POPULATION_INFO, request);
            return ResponseEntity.ok("Batch Job 실행 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/city")
    public ResponseEntity<String> runCityBatch(@RequestBody BatchRunRequest request) {
        try {
            jobTriggerService.run(JobName.CITY_BASIC_INFO, request);
            return ResponseEntity.ok("Batch Job 실행 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }
    @PostMapping("/population/rank")
    public ResponseEntity<String> runPopRankBatch(@RequestBody BatchRunRequest request) {
        try {
            jobTriggerService.run(JobName.POPULATION_RANKING, request);
            return ResponseEntity.ok("Batch Job 실행 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }
    @PostMapping("/city/employ")
    public ResponseEntity<String> runEmpBatch(@RequestBody BatchRunRequest request) {
        try {
            jobTriggerService.run(JobName.CITY_EMPLOYMENT, request);
            return ResponseEntity.ok("Batch Job 실행 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }
    @PostMapping("/city/rank")
    public ResponseEntity<String> runCityRankingBatch(@RequestBody BatchRunRequest request) {
        try {
            jobTriggerService.run(JobName.CITY_RANKING, request);
            return ResponseEntity.ok("Batch Job 실행 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }
}
