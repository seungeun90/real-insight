package io.insight.real.apt.repository.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name="apt_info")
public class AptInfo {
    @Id
    private Long id; // 기본 키

    @Column("adres")
    private String adres;

    @Column("complex_gb_cd")
    private String complexGbCd;

    @Column("complex_gb_nm1")
    private String complexGbNm1; //공시가격기준 명칭

    @Column("complex_gb_nm2")
    private String complexGbNm2; //건축물대장기준 명칭

    @Column("complex_gb_nm3")
    private String complexGbNm3; //도로명대장기준 명칭

    @Column("complex_pk")
    private String complexPk;

    @Column("dong_cnt")
    private Integer dongCnt;

    @Column("pnu")
    private String pnu;

    @Column("unit_cnt")
    private Integer unitCnt;

    @Column("useapr_dt")
    private String useaprDt;


}
