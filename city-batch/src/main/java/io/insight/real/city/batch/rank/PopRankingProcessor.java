package io.insight.real.city.batch.rank;

import io.insight.real.city.dto.PopRankingDto;
import io.insight.real.city.repository.dao.PopulationDao;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PopRankingProcessor implements ItemProcessor<List<PopulationDao>, List<PopRankingDto>> {

    private static final Map<String, Comparator<PopulationDao>> FIELD_COMPARATORS = Map.of(
            "lessThanTeenPer", Comparator.comparingDouble(o -> parseDouble(o.getTeenageLessThanPer())),
            "teenPer", Comparator.comparingDouble(o -> parseDouble(o.getTeenagePer())),
            "twentyPer", Comparator.comparingDouble(o -> parseDouble(o.getTwentyPer())),
            "thirtyPer", Comparator.comparingDouble(o -> parseDouble(o.getThirtyPer())),
            "fortyPer", Comparator.comparingDouble(o -> parseDouble(o.getFortyPer())),
            "fiftyPer", Comparator.comparingDouble(o -> parseDouble(o.getFiftyPer())),
            "sixtyPer", Comparator.comparingDouble(o -> parseDouble(o.getSixtyPer())),
            "moreThanSevenPer", Comparator.comparingDouble(o -> parseDouble(o.getSeventyMoreThanPer())),
            "thirtyToFiftyPer", Comparator.comparingDouble(o ->
                    parseDouble(o.getThirtyPer()) + parseDouble(o.getFortyPer()) + parseDouble(o.getFiftyPer()))

    );
    @Override
    public List<PopRankingDto> process(List<PopulationDao> items) {
        return calculateRankings(items);
    }

    public List<PopRankingDto> calculateRankings(List<PopulationDao> originalList) {
        Map<String, PopRankingDto> rankInfoMap = new HashMap<>();

        // 1️provinceCode + cityCode 기준으로 데이터 그룹화
        Map<String, List<PopulationDao>> groupedByCity = originalList.stream()
                .collect(Collectors.groupingBy(dto -> dto.getProvinceCode() + "-" + dto.getCityCode()));

        // 2️ 필드별 순위 계산 (순차 실행)
        groupedByCity.forEach((key, townList) -> {
            FIELD_COMPARATORS.forEach((field, comparator) -> {
                rankByColumn(townList, rankInfoMap, comparator, field);
            });
        });

        return new ArrayList<>(rankInfoMap.values());
    }
    private void rankByColumn(List<PopulationDao> townList,
                              Map<String, PopRankingDto> rankInfoMap,
                              Comparator<PopulationDao> comparator,
                              String fieldName) {

        // 내림차순 정렬
        List<PopulationDao> sortedList = townList.stream()
                .sorted(comparator.reversed()) // 높은 값이 1등이 되도록 내림차순 정렬
                .toList();

        int rank = 1;
        int realRank = 1;
        PopulationDao prevDto = null;

        for (int i = 0; i < sortedList.size(); i++) {
            PopulationDao dao = sortedList.get(i);
            String key = dao.getProvinceCode() + "-" + dao.getCityCode() + "-" + dao.getTownCode();

            // `rankInfoMap`에서 DTO 조회 또는 생성
            PopRankingDto rankInfo = rankInfoMap.computeIfAbsent(key, k ->
                    new PopRankingDto(dao.getProvinceCode(), dao.getCityCode(), dao.getTownCode(), dao.getTownName()));

            // 동점 여부 확인
            if (prevDto != null && comparator.compare(prevDto, dao) != 0) {
                realRank = rank; // 값이 바뀌면 순위 업데이트
            }
            prevDto = dao;

            // 필드별로 `setRank()` 실행 (필드마다 다른 `rank` 적용)
            setRank(rankInfo, fieldName, realRank);

            rank++; // 다음 순위 증가
        }
    }

    private void setRank(PopRankingDto dto, String field, int rank) {
        switch (field) {
            case "lessThanTeenPer" -> dto.setLessThanTeenPer(rank);
            case "teenPer" -> dto.setTeenPer(rank);
            case "twentyPer" -> dto.setTwentyPer(rank);
            case "thirtyPer" -> dto.setThirtyPer(rank);
            case "fortyPer" -> dto.setFortyPer(rank);
            case "fiftyPer" -> dto.setFiftyPer(rank);
            case "sixtyPer" -> dto.setSixtyPer(rank);
            case "moreThanSevenPer" -> dto.setMoreThanSevenPer(rank);
            case "thirtyToFiftyPer" -> dto.setThirtyToFiftyPer(rank);
            default -> throw new IllegalArgumentException("Unknown field: " + field);
        }
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0; // 변환 실패 시 기본값 0
        }
    }
}
