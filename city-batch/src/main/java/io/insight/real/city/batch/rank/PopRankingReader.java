package io.insight.real.city.batch.rank;

import io.insight.real.city.repository.dao.PopulationDao;
import io.insight.real.city.service.out.PopInfoSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@StepScope
@Component
public class PopRankingReader implements ItemReader<List<PopulationDao>> {
    private final PopInfoSearchRepository popInfoSearchRepository;
    private final String code;
    private boolean isExecuted = false;

    public PopRankingReader(PopInfoSearchRepository popInfoSearchRepository,
                            @Value("#{jobParameters['code']}") String code) {
        this.popInfoSearchRepository = popInfoSearchRepository;
        this.code = code;
    }

    @Override
    public List<PopulationDao> read() {
        log.info("ranking population read started .. code= {}", code);
        if (!isExecuted) { // 한 번만 실행되도록 제어
            isExecuted = true;

            return popInfoSearchRepository.groupByAdmCd(code);

        }
        return null;
    }



}
