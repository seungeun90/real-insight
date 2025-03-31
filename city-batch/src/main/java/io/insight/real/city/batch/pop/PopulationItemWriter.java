package io.insight.real.city.batch.pop;

import io.insight.real.city.dto.PopulationDto;
import io.insight.real.city.dto.request.CityBasicInfoRequest;
import io.insight.real.city.repository.jpa.PopulationRepository;
import io.insight.real.city.repository.entity.CityPopulation;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class PopulationItemWriter implements ItemWriter<List<CityBasicInfoRequest>> {
    private final CityWebClientService cityWebClientService;
    private final PopulationRepository populationRepository;
    private final MessageSenderService messageSenderService;
    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;  // StepExecution 주입
        log.info("PopulationItemWriter Step 시작: {}, 상태: {}", stepExecution.getStepName(), stepExecution.getStatus());
    }
    @Override
    public void write(Chunk<? extends List<CityBasicInfoRequest>> chunk) throws Exception {
        List<CityBasicInfoRequest> items = chunk.getItems().stream()
                .flatMap(List::stream)
                .toList();

        cityWebClientService.fetchPopInfo(items)
                .buffer(100) // 100개씩 모아서 저장 (batch 처리)
                .flatMap(popDataList -> {
                    List<CityPopulation> populations = new ArrayList<>();

                    for (PopulationDto data: popDataList) {
                        String admCd = data.getAdmCd();
                        String provinceCode = admCd.length() >= 2 ? admCd.substring(0, 2) : "";
                        String cityCode = admCd.length() >= 5 ? admCd.substring(2, 5) : "";
                        String townCode = admCd.length() > 5 ? admCd.substring(5) : "";

                        CityPopulation info = CityPopulation.builder()
                                .admCd(admCd)
                                .provinceCode(provinceCode)
                                .cityCode(cityCode)
                                .townCode(townCode)
                                .townName(data.getAdmNm())
                                .teenageLessThanCnt(data.getTeenageLessThanCnt())
                                .teenageLessThanPer(data.getTeenageLessThanPer())
                                .teenageCnt(data.getTeenageCnt())
                                .teenagePer(data.getTeenagePer())
                                .twentyCnt(data.getTwentyCnt())
                                .twentyPer(data.getTwentyPer())
                                .thirtyCnt(data.getThirtyCnt())
                                .thirtyPer(data.getThirtyPer())
                                .fortyCnt(data.getFortyCnt())
                                .fortyPer(data.getFortyPer())
                                .fiftyCnt(data.getFiftyCnt())
                                .fiftyPer(data.getFiftyPer())
                                .sixtyCnt(data.getSixtyCnt())
                                .sixtyPer(data.getSixtyPer())
                                .seventyMoreThanCnt(data.getSeventyMoreThanCnt())
                                .seventyMoreThanPer(data.getSeventyMoreThanPer())
                                .build();
                        populations.add(info);
                    }
                    if (!populations.isEmpty()) {
                        return Mono.defer(() -> {
                            try {
                                populationRepository.saveAll(populations);
                                messageSenderService.publishPopMessage(populations);
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
                .doOnError(error -> log.error("데이터 처리 중 오류 발생: {}", error.getCause().getMessage(), error))
                .then()
                .block();

    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("PopulationItemWriter Step 완료: {}, 최종 상태: {}", stepExecution.getStepName(), stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
