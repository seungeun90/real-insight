package io.insight.real.apt.controller;

import io.insight.real.apt.batch.job.AptItemRequest;
import io.insight.real.apt.batch.job.JobTriggerService;
import io.insight.real.apt.dto.response.AptTradeProfit;
import io.insight.real.apt.service.AptInfoService;
import io.insight.real.apt.service.AptTradeInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
public class AptTradeController {
    private final AptTradeInfoService aptTradeInfoService;
    private final AptInfoService aptInfoService;

    private final JobTriggerService jobTriggerService;

    @GetMapping("/apt/trade/region/{region}")
    public ResponseEntity<?> getAptTrade(@PathVariable("region") String regionCode,
                                         @RequestParam("size") String size){
        List<AptTradeProfit> aptTradeInfo = aptTradeInfoService.getAptTradeInfo(regionCode, size);
        return ResponseEntity.status(HttpStatus.OK).body(aptTradeInfo);
    }


    @PostMapping("/apt/trade")
    public ResponseEntity<?> updateAptTrade(@RequestParam("regionCode") String regionCode,
                                                @RequestParam("startDate") String startDate,@RequestParam("endDate") String endDate){
        aptTradeInfoService.updateAptTradeInfo(regionCode, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/apt")
    public ResponseEntity<?> updateAptInfo(@RequestParam("addr") String addr){
        CompletableFuture.supplyAsync(() -> {
            try {
                jobTriggerService.updateAptInfoJob(new AptItemRequest(addr));
            } catch (JobExecutionException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok("Batch Job 실행 완료");
        }).exceptionally(ex->{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + ex.getMessage());
        });

      /*  try {
            jobTriggerService.updateAptInfoJob(new AptItemRequest(addr));
            return ResponseEntity.ok("Batch Job 실행 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }*/
       // aptInfoService.updateAptTradeInfo(addr);
       // return ResponseEntity.status(HttpStatus.OK).body(null);
        return ResponseEntity.ok("Batch Job 실행 완료");
    }

}
