package io.insight.real.apt.dto;

import lombok.Getter;

/**
 * 전용면적 범위
 * */
@Getter
public enum SupplyAreaRange {
    SIZE_59(56,62),
    SIZE_84(81,87),
    SIZE_99(96,102),
    SIZE_114(111,117),
    SIZE_135(130,140);

    private double start;
    private double end;

    SupplyAreaRange(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public static SupplyAreaRange getRange(String range) {
        try {
            return SupplyAreaRange.valueOf("SIZE_" + range);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 평형 입력: " + range);
        }
    }
}
