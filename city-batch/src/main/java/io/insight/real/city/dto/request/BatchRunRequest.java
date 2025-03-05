package io.insight.real.city.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class BatchRunRequest {
    private String code;
    private String year;

}
