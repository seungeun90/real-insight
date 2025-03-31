package io.insight.real.apt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AptTradeProfit {
    private Long id;
    private String aptNm;
    private String umdNm;
    private Integer buildYear;
    private TradeInfo maxTrade;
    private TradeInfo minTrade;
    private double profitRate;


    public AptTradeProfit(
            Long id,
            String aptNm,
            Integer maxPrice,
            Integer minPrice
    ){
        this.id = id;
        this.aptNm = aptNm;

        this.maxTrade = new TradeInfo(maxPrice);
        this.minTrade = new TradeInfo(minPrice);;
    }

    @Builder
    public AptTradeProfit(
            String aptNm,
            String umdNm,
            Integer buildYear,
            TradeInfo maxTrade,
            TradeInfo minTrade,
            double profitRate) {
        this.aptNm = aptNm;
        this.umdNm = umdNm;
        this.buildYear = buildYear;
        this.maxTrade = maxTrade;
        this.minTrade = minTrade;
        this.profitRate = profitRate;
    }

    public void setProfitRate(double profitRate) {
        this.profitRate = profitRate;
    }
}
