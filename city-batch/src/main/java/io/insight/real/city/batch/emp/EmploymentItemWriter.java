package io.insight.real.city.batch.emp;

import io.insight.real.city.dto.EmploymentDto;
import io.insight.real.city.dto.request.CityBasicInfoRequest;
import io.insight.real.city.repository.entity.Employment;
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
public class EmploymentItemWriter implements ItemWriter<List<CityBasicInfoRequest>> {
    private final CityWebClientService cityWebClientService;
    private final MessageSenderService messageSenderService;
    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;  //StepExecution 주입
        log.info("EmploymentItemWriter Step 시작: {}, 상태: {}", stepExecution.getStepName(), stepExecution.getStatus());
    }
    @Override
    public void write(Chunk<? extends List<CityBasicInfoRequest>> chunk) throws Exception {
        List<CityBasicInfoRequest> items = chunk.getItems().stream()
                .flatMap(List::stream)
                .toList();

        cityWebClientService.fetchCityEmpInfos(items)
                .buffer(100) //  100개씩 모아서 저장 (batch 처리)
                .flatMap(employments -> {
                    List<Employment> employment = new ArrayList<>();

                    for (EmploymentDto data: employments) {
                        Employment emp = Employment.builder()
                                .provinceCode(data.getProvinceCode())
                                .cityCode(data.getCityCode())
                                .townCode(data.getTownCode())
                                .townName(data.getTownName())
                                .year(data.getYear())
                                .corpCnt(data.getCorpCnt())
                                .employCnt(data.getEmployCnt())
                                .build();
                        employment.add(emp);
                    }
                    if (!employment.isEmpty()) {
                        return Mono.defer(() -> {
                            try {
                                messageSenderService.publishEmpMessage(employment); //RabbitMQ 메시지 전송
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
        log.info("EmploymentItemWriter Step 완료: {}, 최종 상태: {}", stepExecution.getStepName(), stepExecution.getStatus());
        return stepExecution.getExitStatus();
    }
}
