package io.insight.real.infra.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("city_ranking")
public class CityRanking {

    private String provinceCode;
    private String cityCode;
    private String year;
    private int totalPopRank;
    private int popDensityRank;
    private int agedChildIdxRank;
    private int familyCntRank;
    private int avgFamilyCntRank;
    private int employCntRank;
    private int corpCntRank;

    public static String getCollectionName() {
        return "city_ranking";
    }
}
