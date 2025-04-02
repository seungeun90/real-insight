package io.insight.real.apt.controller;
import io.insight.real.apt.dto.AptTradeProfit;
import io.insight.real.apt.service.in.AptTradeInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AptTradeController {
    private final AptTradeInfoService aptTradeInfoService;

    @GetMapping("/apt/trade/region/{region}")
    public ResponseEntity<?> getAptTrade(@PathVariable("region") String regionCode,
                                         @RequestParam("size") String size) {
        List<AptTradeProfit> aptTradeInfo = aptTradeInfoService.getAptTradeInfo(regionCode, size);
        return ResponseEntity.status(HttpStatus.OK).body(aptTradeInfo);
    }

}