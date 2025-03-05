package io.insight.real.city.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;


@Getter
@AllArgsConstructor
public class SgisToken {
    private String accessToken;
    private Instant expiryTime;

    /**
     * 토큰의 만료 여부를 확인.
     * @return 토큰이 만료되었는지 여부
     */
    public boolean isTokenExpired() {
        return expiryTime == null || Instant.now().isAfter(expiryTime);
    }

}
