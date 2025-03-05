package io.insight.real.city.batch.rank;

import io.insight.real.city.dto.PopRankingDto;
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
public class PopRankingWriter implements ItemWriter<List<PopRankingDto>> {

    private final MessageSenderService messageSenderService;

    @Override
    public void write(Chunk<? extends List<PopRankingDto>> chunk) throws Exception {
        List<PopRankingDto> items = chunk.getItems().stream()
                .flatMap(List::stream)
                .toList();

        messageSenderService.publishPopRankMessage(items);
    }
}
