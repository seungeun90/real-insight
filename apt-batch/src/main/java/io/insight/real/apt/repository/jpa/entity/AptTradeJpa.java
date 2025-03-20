package io.insight.real.apt.repository.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "apt_trade")
public class AptTradeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가 설정
    private Long id;

    @Column(name = "sgg_cd") // 지역코드
    private String sggCd;

    @Column(name = "umd_nm") // 법정동
    private String umdNm;

    @Column(name = "apt_dong") // 아파트 동명
    private String aptDong;

    @Column(name = "apt_nm") // 단지명
    private String aptNm;

    @Column(name = "build_year") // 건축년도
    private Integer buildYear;

    @Column(name = "deal_amount") // 거래금액
    private String dealAmount;

    @Column(name = "deal_day") // 계약일
    private Integer dealDay;

    @Column(name = "deal_month") // 계약월
    private Integer dealMonth;

    @Column(name = "deal_year") // 계약년도
    private Integer dealYear;

    @Column(name = "exclu_use_ar") // 전용면적
    private double excluUseAr;

    @Column(name = "floor") // 층
    private Integer floor;

    @Column(name = "jibun") // 지번
    private String jibun;

    @Column(name = "land_leasehold_gbn") // 토지임대부 아파트 여부
    private String landLeaseholdGbn;

    @Column(name = "dealing_gbn") // 거래유형
    private String dealingGbn;

    @Column(name = "cdeal_type") // 해제 여부
    private String cdealType;

    @Column(name = "cdeal_day") // 해제 사유 발생일
    private String cdealDay;

    @Column(name = "buyer_gbn") // 매수자
    private String buyerGbn;

    @Column(name = "sler_gbn") // 매도자
    private String slerGbn;

    @Column(name = "rgst_date") // 등기일자
    private String rgstDate;

    @Column(name = "estate_agent_sggNm") // 중개사 소재지
    private String estateAgentSggNm;
}
