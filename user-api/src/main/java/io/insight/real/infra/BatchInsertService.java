package io.insight.real.infra;


import io.insight.real.dto.ProvinceDto;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.CityPopulation;
import io.insight.real.dto.CityRankingData;
import io.insight.real.dto.PopRankingData;
import io.insight.real.infra.repository.entity.Employment;
import io.insight.real.infra.repository.jpa.CityDataRepository;
import io.insight.real.infra.repository.jpa.EmploymentRepository;
import io.insight.real.infra.repository.jpa.PopulationDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class BatchInsertService {
    private final MessageQueue<CityPopulation> populationDataQueue;
    private final MessageQueue<CityBasicInfo> cityDataQueue;
    private final MessageQueue<Employment> employmentQueue;
    private final PopulationDataRepository populationRepository;
    private final CityDataRepository cityDataRepository;
    private final MongoTemplate mongoTemplate;
    private final EmploymentRepository employmentRepository;


    @Scheduled(cron = "0 0/10 * * * *")
    public void insertPopBatchData() {
        if (populationDataQueue.size() > 0) {
            List<CityPopulation> batchList = populationDataQueue.getBatch();
            populationRepository.saveAll(batchList);
            log.info("Pop Data Batch Insert 완료: " + batchList.size() + "개 저장됨");
        }
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void insertEmploymentBatchData() {
        if (employmentQueue.size() > 0) {
            List<Employment> batchList = employmentQueue.getBatch();
            employmentRepository.saveAll(batchList);
            log.info("Emp Data Batch Insert 완료: " + batchList.size() + "개 저장됨");
        }
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void insertCityBatchData() {
        if (cityDataQueue.size() > 0) {
            List<CityBasicInfo> batchList = cityDataQueue.getBatch();
            cityDataRepository.saveAll(batchList);
            log.info("City Data Batch Insert 완료: " + batchList.size() + "개 저장됨");
        }
    }
    public void savePopRankingData(PopRankingData popRankingData) {
        mongoTemplate.insert(popRankingData, PopRankingData.getCollectionName());
    }
    public void saveCityRankingData(CityRankingData cityRankingData) {
        mongoTemplate.insert(cityRankingData, CityRankingData.getCollectionName());
    }

    public void saveDistrictData(Map<String, Object> message) {
        mongoTemplate.insert(message, "district");
    }
}
