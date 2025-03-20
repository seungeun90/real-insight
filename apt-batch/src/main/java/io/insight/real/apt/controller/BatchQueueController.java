package io.insight.real.apt.controller;

import io.insight.real.apt.dto.BatchJobRequest;
import io.insight.real.apt.service.BatchReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BatchQueueController {

    private final BatchReserveService batchReserveService;

    @PostMapping("/apt/trade/batch")
    public ResponseEntity<?> submitAptTradeJob(@RequestBody BatchJobRequest batchJobRequest) {
        batchReserveService.enqueueAptTradeJob(batchJobRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping("/apt/batch")
    public ResponseEntity<?> getAptInfoJob(@RequestBody BatchJobRequest batchJobRequest) {
        batchReserveService.enqueueAptInfoJob(batchJobRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
