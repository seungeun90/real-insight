package io.insight.real.apt.controller;

import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.dto.response.ResponseData;
import io.insight.real.apt.service.in.BatchReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BatchQueueController {

    private final BatchReserveService batchReserveService;


    @PostMapping("/schedules/apt-trade")
    public ResponseEntity<?> submitAptTradeJob(@RequestBody BatchJobRequest batchJobRequest) {
        batchReserveService.enqueueAptTradeJob(batchJobRequest);
        return ResponseData.success(null);
    }

    @PostMapping("/schedules/apt")
    public ResponseEntity<?> submitAptInfoJob(@RequestBody BatchJobRequest batchJobRequest) {
        batchReserveService.enqueueAptInfoJob(batchJobRequest);
        return ResponseData.success(null);
    }

}
