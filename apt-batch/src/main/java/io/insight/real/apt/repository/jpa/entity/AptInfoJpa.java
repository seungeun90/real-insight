package io.insight.real.apt.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "apt_info")
public class AptInfoJpa {

    @Id
    private Long id; // 기본 키

    @Column(name = "adres")
    private String adres;

    @Column(name = "complex_gb_cd")
    private String complexGbCd;

    @Column(name = "complex_gb_nm1")
    private String complexGbNm1;

    @Column(name = "complex_gb_nm2")
    private String complexGbNm2;

    @Column(name = "complex_gb_nm3")
    private String complexGbNm3;

    @Column(name = "complex_pk")
    private String complexPk;

    @Column(name = "dong_cnt")
    private Integer dongCnt;

    @Column(name = "pnu")
    private String pnu;

    @Column(name = "unit_cnt")
    private Integer unitCnt;

    @Column(name = "useapr_dt")
    private String useaprDt;
}
