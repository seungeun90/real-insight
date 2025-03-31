package io.insight.real.city.batch.city;

import io.insight.real.city.dto.CityBasicDto;
import io.insight.real.city.dto.request.CityBasicInfoRequest;
import io.insight.real.city.repository.jpa.CityBasicRepository;
import io.insight.real.city.repository.entity.CityBasicInfo;
import io.insight.real.city.service.CityWebClientService;
import io.insight.real.city.service.MessageSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class CityBasicItemWriter implements ItemWriter<List<CityBasicInfoRequest>> {

    private final CityWebClientService cityWebClientService;
    private final CityBasicRepository cityRepository;
    private final MessageSenderService messageSenderService;
    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;  // StepExecution 주입
        log.info("CityBasicItemWriter Step 시작: {}, 상태: {}", stepExecution.getStepName(), stepExecution.getStatus());
    }

    @Override
    public void write(Chunk<? extends List<CityBasicInfoRequest>> chunk) {
        List<CityBasicInfoRequest> items = chunk.getItems().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());


        cityWebClientService.fetchCityBasicInfos(items)
                .buffer(100) //  100개씩 모아서 저장 (batch 처리)
                .flatMap(cityDataList -> {
                    List<CityBasicInfo> basicInfos = new ArrayList<>();

                    for (CityBasicDto data : cityDataList) {
                        String admCd = data.getAdmCd();
                        String provinceCode = admCd.length() >= 2 ? admCd.substring(0, 2) : "";
                        String cityCode = admCd.length() >= 5 ? admCd.substring(2, 5) : "";
                        String townCode = admCd.length() > 5 ? admCd.substring(5) : "";
                        CityBasicInfo info = CityBasicInfo.builder()
                                .admCd(admCd)
                                .provinceCode(provinceCode)
                                .cityCode(cityCode)
                                .townCode(townCode)
                                .totalPopulation(data.getTotalPop())
                                .averageAge(data.getAvgAge())
                                .populationDensity(data.getDensity())
                                .agingChildIndex(data.getAgedChildIdx())
                                .householdCount(data.getFamily())
                                .averageHouseholdSize(data.getAvgFamilyCnt())
                                .employCnt(data.getEmployCnt())
                                .corpCnt(data.getCorpCnt())
                                .year(data.getYear())
                                .build();
                        basicInfos.add(info);
                    }

                    // 저장할 데이터가 있으면 비동기로 저장
                    if (!basicInfos.isEmpty()) {
                        return Mono.defer(() -> {
                            try {
                                cityRepository.saveAll(basicInfos);
                                messageSenderService.publishCityMessage(basicInfos);

                                return Mono.empty();
                            } catch (Exception e) {
                                log.error("데이터 저장 중 오류 발생: {}", e.getMessage(), e);
                                return Mono.error(e);
                            }
                        })
                        .doOnSuccess(s -> log.info("데이터 처리 완료"));
                    }
                    return Mono.empty();
                })
                .doOnError(error -> log.error("데이터 처리 중 오류 발생: {}", error.getMessage()))
                .then()
                .block();
    }
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("CityBasicItemWriter Step 완료: {}, 최종 상태: {}", stepExecution.getStepName(), stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
