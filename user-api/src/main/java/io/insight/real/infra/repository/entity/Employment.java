package io.insight.real.infra.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employment that = (Employment) o;
        return Objects.equals(id, that.id) && Objects.equals(provinceCode, that.provinceCode) && Objects.equals(cityCode, that.cityCode) && Objects.equals(townCode, that.townCode) && Objects.equals(townName, that.townName) && Objects.equals(employCnt, that.employCnt) && Objects.equals(corpCnt, that.corpCnt) && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, provinceCode, cityCode, townCode, townName, employCnt, corpCnt, year);
    }
}
