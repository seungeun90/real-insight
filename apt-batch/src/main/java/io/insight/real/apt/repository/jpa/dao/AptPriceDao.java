package io.insight.real.apt.repository.jpa.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AptPriceDao {
    private Long id;
    private String aptNm;
    private String umdNm;
    private Integer buildYear;
    private Integer dealYear;
    private Integer dealMonth;
    private Integer dealDay;
    private Integer floor;
    private String dealAmount;
    private double excluUseAr;
}
