package io.insight.real.apt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeInfo {

    private String dealDate;
    private Integer floor;
    private Integer price;
    private double excluAr;

    public TradeInfo(Integer price) {
        this.price = price;
    }

    @Builder
    public TradeInfo(String dealDate,
                     Integer floor,
                     Integer price,
                     double excluAr) {
        this.dealDate = dealDate;
        this.floor = floor;
        this.price = price;
        this.excluAr = excluAr;
    }

}
