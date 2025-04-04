package io.insight.real.infra.repository.mapper;

import io.insight.real.dto.CityBasicInfoData;
import io.insight.real.dto.CityEmploymentData;
import io.insight.real.dto.CityRankingData;
import io.insight.real.infra.repository.entity.CityBasicInfo;
import io.insight.real.infra.repository.entity.CityRanking;
import io.insight.real.infra.repository.entity.Employment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CityInfoMapper {
    List<CityBasicInfoData> toCityDtoList(List<CityBasicInfo> items);
    List<CityEmploymentData> toEmpDtoList(List<Employment> items);
    CityBasicInfoData toCityDto(CityBasicInfo item);
    CityRankingData toRankingDto(CityRanking item);
}
