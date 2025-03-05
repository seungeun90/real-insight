package io.insight.real.infra.repository.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Document(collection = "employment")
public class Employment {
    @Id
    private String id; // 기본 키
    private String provinceCode;
    private String cityCode;
    private String townCode;
    private String townName;
    private String employCnt; //종사자수
    private String corpCnt; //사업체 수
    private String year;
}
