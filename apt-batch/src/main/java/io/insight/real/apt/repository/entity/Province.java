package io.insight.real.apt.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="province")
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code; // 시/도 + 군/구 코드 (5자리)
    private String name;
    private String status; // 폐지 여부

    @Builder
    public Province(String code, String name, String status) {
        this.code = code;
        this.name = name;
        this.status = status;
    }
}
