package io.insight.real.city.batch.rank;

import io.insight.real.city.dto.CityBasicDto;
import io.insight.real.city.dto.RankedInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
public class CityInfoRankingProcessor implements ItemProcessor<List<CityBasicDto>, List<RankedInfoDto>> {
    private static final Map<String, Comparator<CityBasicDto>> FIELD_COMPARATORS = Map.of(
            "totalPop", Comparator.comparingInt(o -> parseInt(o.getTotalPop())),
            "density", Comparator.comparingDouble(CityBasicDto::getDensity),
            "agedChildIdx", Comparator.comparingDouble(CityBasicDto::getAgedChildIdx),
            "family", Comparator.comparingInt(o -> parseInt(o.getFamily())),
            "avgFamilyCnt", Comparator.comparingDouble(CityBasicDto::getAvgFamilyCnt),
            "employCnt", Comparator.comparingInt(o -> parseInt(o.getEmployCnt())),
            "corpCnt", Comparator.comparingInt(o -> parseInt(o.getCorpCnt()))
    );

    @Override
    public List<RankedInfoDto> process(List<CityBasicDto> items) throws Exception {
        log.info("processing items.size ={}", items.size());
        return calculateRankings(items);
    }

    private List<RankedInfoDto> calculateRankings(List<CityBasicDto> originalList) throws ExecutionException, InterruptedException {

        // ConcurrentHashMap을 사용하여 스레드 안전성과 병렬 처리 효율성 확보
        ConcurrentMap<String, RankedInfoDto> rankInfoMap = new ConcurrentHashMap<>();

        // ExecutorService 생성
        ExecutorService executor = Executors.newFixedThreadPool(FIELD_COMPARATORS.size());

        // CompletableFuture 리스트 생성
        List<CompletableFuture<Void>> futures = FIELD_COMPARATORS.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(
                        () -> calculateAndSetRank(originalList, rankInfoMap, entry.getValue(), entry.getKey()),
                        executor
                ))
                .toList();

        // 모든 작업이 완료될 때까지 대기
        for (CompletableFuture<Void> future : futures) {
            future.get();
        }

        // ExecutorService 종료
        executor.shutdown();

        // Map의 값들을 List로 변환하여 반환
        return new ArrayList<>(rankInfoMap.values());
    }

    private void calculateAndSetRank(List<CityBasicDto> originalList, ConcurrentMap<String, RankedInfoDto> rankInfoMap,
                                     Comparator<CityBasicDto> comparator
            ,String fieldName) {
        // 원본 리스트를 정렬
        List<CityBasicDto> sortedList = originalList.stream()
                .sorted(comparator.reversed())
                .toList();

        int rank = 1;
        for (int i = 0; i < sortedList.size(); i++) {
            CityBasicDto current = sortedList.get(i);
            // 고유한 키 생성 (예: provinceCode + cityCode + year)
            String key = current.getProvinceCode() + current.getCityCode() + current.getYear();
            // RankInfo 객체 찾기 또는 생성
            RankedInfoDto rankInfo = rankInfoMap.computeIfAbsent(key, k ->
                    new RankedInfoDto(current.getProvinceCode(), current.getCityCode(), current.getYear())
            );
            // 이전 항목과 값이 다르면 순위 갱신
            if (i > 0 && comparator.compare(sortedList.get(i - 1), current) != 0) {
                rank = i + 1;
            }
            // 해당 필드의 순위 설정
            setRank(rankInfo, fieldName, rank);
        }
    }

    private void setRank(RankedInfoDto rankInfo, String fieldName, int rank) {
        switch (fieldName) {
            case "totalPop":
                rankInfo.setTotalPopRank(rank);
                break;
            case "density":
                rankInfo.setPopDensityRank(rank);
                break;
            case "agedChildIdx":
                rankInfo.setAgedChildIdxRank(rank);
                break;
            case "family":
                rankInfo.setFamilyCntRank(rank);
                break;
            case "avgFamilyCnt":
                rankInfo.setAvgFamilyCntRank(rank);
                break;
            case "employCnt":
                rankInfo.setEmployCntRank(rank);
                break;
            case "corpCnt":
                rankInfo.setCorpCntRank(rank);
                break;
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }
    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // 변환 실패 시 기본값 0
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
