package io.insight.real.city.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter @Setter
public class ApiResponse {
    private String id;
    private String trId;
    private Integer errCd;
    private String errMsg;
    private Object result;
    private ZonedDateTime timestamp;

    public boolean hasFailed(){
        return this.errCd != 0;
    }

} 