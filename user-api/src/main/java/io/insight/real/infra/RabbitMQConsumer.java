package io.insight.real.infra;

import com.rabbitmq.client.Channel;
import io.insight.real.dto.ProvinceDto;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.CityPopulation;
import io.insight.real.dto.CityRankingData;
import io.insight.real.dto.PopRankingData;
import io.insight.real.infra.repository.entity.Employment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitMQConsumer {

    private final MessageQueue<CityPopulation> populationDataQueue;
    private final MessageQueue<CityBasicInfo> cityDataQueue;
    private final MessageQueue<Employment> employmentDataQueue;
    private final BatchInsertService batchInsertService;

    @RabbitListener(queues = "pop-queue", ackMode = "MANUAL")
    public void receivePopMessage(CityPopulation message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received population Messages.. ");
        try {
            populationDataQueue.addMessage(message);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Message processing failed: {}", e.getMessage());
            // DLQ로 이동
            channel.basicNack(tag, false, false); // 메시지를 즉시 DLQ(dead-queue)로 이동
        }
    }

    @RabbitListener(queues = "city-queue", ackMode = "MANUAL")
    public void receiveCityMessage(CityBasicInfo message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received CityBasicInfo Messages.. ");
       try {
            cityDataQueue.addMessage(message);
           channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Message processing failed: {}", e.getMessage());
            // DLQ로 이동
           channel.basicNack(tag, false, false);// 메시지를 즉시 DLQ(dead-queue)로 이동
        }
    }
    @RabbitListener(queues = "emp-queue", ackMode = "MANUAL")
    public void receiveEmpMessage(Employment message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received emp Messages.. ");
        try {
            employmentDataQueue.addMessage(message);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Message processing failed: {}", e.getMessage());
            // DLQ로 이동
            channel.basicNack(tag, false, false); // 메시지를 즉시 DLQ(dead-queue)로 이동
        }
    }
    @RabbitListener(queues = "pop-rank-queue", ackMode = "MANUAL")
    public void receivePopRankMessage(PopRankingData message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received pop Ranking Messages.. ");

        try {
            batchInsertService.savePopRankingData(message);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Message processing failed: {}", e.getMessage());
            // DLQ로 이동
            channel.basicNack(tag, false, false); // 메시지를 즉시 DLQ(dead-queue)로 이동
        }
    }
    @RabbitListener(queues = "city-rank-queue", ackMode = "MANUAL")
    public void receiveRankMessage(CityRankingData message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received city Ranking Messages.. ");

        try {
            batchInsertService.saveCityRankingData(message);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Message processing failed: {}", e.getMessage());
            // DLQ로 이동
            channel.basicNack(tag, false, false);// 메시지를 즉시 DLQ(dead-queue)로 이동
        }
    }
    @RabbitListener(queues = "district-queue", ackMode = "MANUAL")
    public void receiveDistrictMessage(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.info("Received city Ranking Messages.. ");

        try {
            batchInsertService.saveDistrictData(message);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Message processing failed: {}", e.getMessage());
            // DLQ로 이동
            channel.basicNack(tag, false, false);// 메시지를 즉시 DLQ(dead-queue)로 이동
        }
    }
}