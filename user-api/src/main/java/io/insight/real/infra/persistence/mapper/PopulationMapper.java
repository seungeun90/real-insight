package io.insight.real.infra.persistence.mapper;

import io.insight.real.dto.PopulationData;
import io.insight.real.infra.persistence.entity.CityPopulation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PopulationMapper {

    List<PopulationData> toDtoList(List<CityPopulation> item);
}
