package io.insight.real.city.batch.rank;

import io.insight.real.city.dto.CityBasicDto;
import io.insight.real.city.service.out.CityInfoSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@StepScope
@Component
public class CityInfoRankingReader  implements ItemReader<List<CityBasicDto>> {
    private final CityInfoSearchRepository cityInfoSearchRepository;
    private final String code;
    private final String year;
    private boolean isExecuted = false;

    public CityInfoRankingReader(CityInfoSearchRepository cityInfoSearchRepository,
                            @Value("#{jobParameters['code']}") String code,
                            @Value("#{jobParameters['year']}") String year) {
        this.cityInfoSearchRepository = cityInfoSearchRepository;
        this.code = code;
        this.year = year;
    }

    @Override
    public List<CityBasicDto> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("ranking city read started .. code= {}, year = {} ", code, year);
        if (!isExecuted) { // 한 번만 실행되도록 제어
            isExecuted = true;

            return cityInfoSearchRepository.groupByAdmCd(code, year);

        }
        return null; // 이후에는 null을 반환하여 Batch 종료
    }

}
