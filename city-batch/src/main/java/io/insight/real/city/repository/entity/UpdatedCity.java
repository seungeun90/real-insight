package io.insight.real.city.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "updated_district")
public class UpdatedCity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(name = "year", length = 50)
    private String year;

    @Column(name = "adm_cd", length = 100)
    private String admCd; // 시도 코드

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UpdatedCity(String year, String admCd) {
        this.year = year;
        this.admCd = admCd;
    }

}
