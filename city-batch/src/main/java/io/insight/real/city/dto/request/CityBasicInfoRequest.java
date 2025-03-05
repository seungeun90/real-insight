package io.insight.real.city.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CityBasicInfoRequest extends RequestToken {
    private String year;

    public CityBasicInfoRequest(String admCd, String year) {
        super(admCd);
        this.year = year;
    }
    public CityBasicInfoRequest(String admCd) {
        super(admCd);
        this.year = null;
    }
}
