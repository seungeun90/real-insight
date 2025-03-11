package io.insight.real.apt.repository.r2dbc;

import io.insight.real.apt.repository.entity.AptInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AptInfoCustomRepository {
    private final DatabaseClient databaseClient;

    public Mono<Void> bulkUpsert(List<AptInfo> entities) {
        if (entities.isEmpty()) {
            return Mono.empty();
        }

        //  Query 생성 (Batch 처리)
        String sql = """
            INSERT INTO apt_info (complex_pk, adres, complex_gb_cd, complex_gb_nm1, complex_gb_nm2, complex_gb_nm3, 
                                  dong_cnt, pnu, unit_cnt, useapr_dt)
            VALUES (:complexPk, :adres, :complexGbCd, :complexGbNm1, :complexGbNm2, :complexGbNm3, 
                    :dongCnt, :pnu, :unitCnt, :useaprDt)
            ON DUPLICATE KEY UPDATE
                adres = VALUES(adres),
                complex_gb_cd = VALUES(complex_gb_cd),
                complex_gb_nm1 = VALUES(complex_gb_nm1),
                complex_gb_nm2 = VALUES(complex_gb_nm2),
                complex_gb_nm3 = VALUES(complex_gb_nm3),
                dong_cnt = VALUES(dong_cnt),
                pnu = VALUES(pnu),
                unit_cnt = VALUES(unit_cnt),
                useapr_dt = VALUES(useapr_dt)
        """;
        return Flux.fromIterable(entities)
                .flatMap(entity -> {
                    DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql)
                            .bind("complexPk", entity.getComplexPk())
                            .bind("adres", entity.getAdres())
                            .bind("complexGbCd", entity.getComplexGbCd());

                    // ✅ `complexGbNm1` 값이 `null`이면 `bindNull()`, 아니면 `bind()`
                    if (entity.getComplexGbNm1() != null) {
                        spec = spec.bind("complexGbNm1", entity.getComplexGbNm1());
                    } else {
                        spec = spec.bindNull("complexGbNm1", String.class);
                    }

                    if (entity.getComplexGbNm2() != null) {
                        spec = spec.bind("complexGbNm2", entity.getComplexGbNm2());
                    } else {
                        spec = spec.bindNull("complexGbNm2", String.class);
                    }

                    if (entity.getComplexGbNm3() != null) {
                        spec = spec.bind("complexGbNm3", entity.getComplexGbNm3());
                    } else {
                        spec = spec.bindNull("complexGbNm3", String.class);
                    }

                    if (entity.getDongCnt() != null) {
                        spec = spec.bind("dongCnt", entity.getDongCnt());
                    } else {
                        spec = spec.bindNull("dongCnt", Integer.class);
                    }

                    if (entity.getPnu() != null) {
                        spec = spec.bind("pnu", entity.getPnu());
                    } else {
                        spec = spec.bindNull("pnu", String.class);
                    }

                    if (entity.getUnitCnt() != null) {
                        spec = spec.bind("unitCnt", entity.getUnitCnt());
                    } else {
                        spec = spec.bindNull("unitCnt", Integer.class);
                    }

                    if (entity.getUseaprDt() != null) {
                        spec = spec.bind("useaprDt", entity.getUseaprDt());
                    } else {
                        spec = spec.bindNull("useaprDt", String.class);
                    }

                    // ✅ 최종적으로 SQL 실행
                    return spec.fetch().rowsUpdated();
                })
                .then();


    }
}
