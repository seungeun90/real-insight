package io.insight.real.apt.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="district")
public class LegalDistrict {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long regionId;
    private String code;   // 법정동 전체 코드
    private String name; // 법정동명
    private String status; // 폐지 여부
    @Builder
    public LegalDistrict(
            Long regionId,
            String code,
            String name,
            String status) {
        this.regionId = regionId;
        this.code = code;
        this.name = name;
        this.status = status;
    }
}
