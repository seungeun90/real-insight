package io.insight.real.apt.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "apt_trade")
public class AptTrade {
    @Id
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column("sgg_cd") //지역코드
    private String sggCd;

    @Column("umd_nm")//"법정동"
    private String umdNm;

    @Column("apt_dong")//아파트 동명
    private String aptDong;

    @Column("apt_nm")//단지명
    private String aptNm;

    @Column("build_year")//건축년도
    private int buildYear;

    @Column("deal_amount")//거래금액
    private String dealAmount;

    @Column("deal_day")//계약일
    private int dealDay;

    @Column("deal_month")//계약월
    private int dealMonth;

    @Column("deal_year")//계약년도
    private int dealYear;

    @Column("exclu_use_ar")//전용면적
    private double excluUseAr;

    @Column("floor")//층
    private int floor;

    @Column("jibun")//지번
    private String jibun;

    @Column("land_leasehold_gbn")//토지임대부 아파트 여부
    private String landLeaseholdGbn;

    @Column("dealing_gbn")//거래유형
    private String dealingGbn;

    @Column("cdeal_type")//해제여부
    private String cdealType;

    @Column("cdeal_day")//해제사유발생일
    private String cdealDay;

    @Column("buyer_gbn")//매수자
    private String buyerGbn;

    @Column("sler_gbn")//매도자
    private String slerGbn;

    @Column("rgst_date")//등기일자
    private String rgstDate;

    @Column("estate_agent_sggNm")//중개사소재지
    private String estateAgentSggNm;
}
