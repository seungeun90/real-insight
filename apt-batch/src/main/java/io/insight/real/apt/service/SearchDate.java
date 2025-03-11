package io.insight.real.apt.service;

import java.time.YearMonth;

public enum SearchDate {
    HIGHEST(YearMonth.of(21,1),YearMonth.of(22,12)),
    Lowest(YearMonth.of(21,1),null);

    private YearMonth startYearMonth;
    private YearMonth endYearMonth;

    SearchDate(YearMonth startYearMonth, YearMonth endYearMonth) {
        this.startYearMonth = startYearMonth;
        this.endYearMonth = endYearMonth;
    }

}
