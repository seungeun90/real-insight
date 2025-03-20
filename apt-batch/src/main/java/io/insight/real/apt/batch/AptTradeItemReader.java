package io.insight.real.apt.batch;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.insight.real.apt.config.ApiProperties;
import io.insight.real.apt.dto.response.ApiResponse;
import io.insight.real.apt.dto.response.AptResponse;
import io.insight.real.apt.repository.jpa.AptTradeQueryRepository;
import io.insight.real.apt.repository.mapper.AptTradeMapper;
import io.insight.real.apt.repository.r2dbc.AptTradeRepository;
import io.insight.real.apt.service.CommonWebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@StepScope
public class AptTradeItemReader implements ItemReader<URI> {
    private String regionCode;
    private String startDate;
    private String endDate;

    private List<String> monthList;
    private Map<String, Integer> MONTH_PAGING = new HashMap<>();
    private Map<String, Integer> TOTAL_PAGES = new HashMap<>();

    private final ApiProperties apiProperties;
    private final CommonWebClientService webClientService;
    private final ApiResponseUtil apiResponseUtil;

    public AptTradeItemReader(
            CommonWebClientService webClientService,
            ApiProperties apiProperties,
            ApiResponseUtil apiResponseUtil,
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate,
            @Value("#{jobParameters['regionCode']}") String regionCode) {
        this.webClientService = webClientService;
        this.apiProperties = apiProperties;
        this.apiResponseUtil = apiResponseUtil;
        this.startDate = startDate;
        this.endDate = endDate;
        this.regionCode = regionCode;
    }

    @Override
    public URI read() {
        if(!StringUtils.hasText(startDate)){
            throw new IllegalArgumentException("startDate cannot be null");
        }
        if(!StringUtils.hasText(endDate)) {
            endDate = startDate;
        }

        if (monthList == null) {
            if (!StringUtils.hasText(startDate)) {
                throw new IllegalArgumentException("startDate cannot be null");
            }
            if (!StringUtils.hasText(endDate)) {
                endDate = startDate;
            }
            monthList = getMonthList(startDate, endDate);
            monthList.forEach(month -> {
                MONTH_PAGING.put(month, 1);
            });
        }

        for (String month : monthList) {
            Integer curPage = MONTH_PAGING.get(month);
            Integer totalPages = TOTAL_PAGES.getOrDefault(month, -1);

            // totalPages를 아직 안 구했다면 첫 페이지 호출해서 계산
            if (totalPages == -1) {
                ApiResponse response = webClientService.executeXmlRequest(
                        buildUrl(1, regionCode, month),
                        BodyInserters.empty(),
                        apiResponseUtil::parseResponse
                ).blockFirst();

                if (response == null) {
                    // 데이터가 없으면 이 month는 스킵하고 다음 month로
                    MONTH_PAGING.put(month, -1);
                    continue;
                }

                int totalCount = response.getBody().getTotalCount();
                int numOfRows = response.getBody().getNumOfRows();
                totalPages = (int) Math.ceil((double) totalCount / numOfRows);

                TOTAL_PAGES.put(month, totalPages);
            }

            // 현재 month의 페이지가 totalPages를 넘으면 다음 month로
            if (curPage >= totalPages) {
                continue;
            }
            URI uri = buildUrl(curPage, regionCode, month);
            MONTH_PAGING.put(month, curPage + 1);

            return uri;
        }

        return null;
    }

    private URI buildUrl(int pageNo, String regionCode, String dealDate) {
        String baseUrl = "https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade";
        String serviceKey = apiProperties.getKey();
        int numOfRows = 20;
        URI uri = URI.create(baseUrl
                + "?LAWD_CD="+regionCode
                + "&DEAL_YMD="+dealDate
                + "&serviceKey="+serviceKey
                + "&pageNo=" +pageNo
                + "&numOfRows="+numOfRows);
        return uri;
    }
    public static List<String> getMonthList(String startDate, String endDate) {
        // startDate와 endDate를 YearMonth 형식으로 변환
        YearMonth start = parseYearMonth(startDate);
        YearMonth end = parseYearMonth(endDate);

        List<String> monthList = new ArrayList<>();
        while (!start.isAfter(end)) {
            monthList.add(start.toString().replace("-", "")); // "yyyyMM" 형식으로 저장
            start = start.plusMonths(1);
        }
        return monthList;
    }

    private static YearMonth parseYearMonth(String date) {
        date = date.replace(".", ""); // "."을 제거하여 "yyyyMM" 형식으로 정리
        return YearMonth.of(
                Integer.parseInt(date.substring(0, 4)), // 연도
                Integer.parseInt(date.substring(4, 6))  // 월
        );
    }
}
