package io.insight.real.city.batch.rank;

import io.insight.real.city.dto.RankedInfoDto;
import io.insight.real.city.service.MessageSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CityInfoRankingWriter implements ItemWriter<List<RankedInfoDto>> {

    private final MessageSenderService messageSenderService;

    @Override
    public void write(Chunk<? extends List<RankedInfoDto>> chunk) throws Exception {
        List<RankedInfoDto> items = chunk.getItems().stream()
                .flatMap(List::stream)
                .toList();

        messageSenderService.publishCityRankMessage(items);
    }

}
