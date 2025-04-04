package io.insight.real.infra.persistence.mapper;

import io.insight.real.dto.DistrictData;
import io.insight.real.infra.persistence.entity.District;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DistrictMapper {
    List<DistrictData> toDtoList(List<District> item);
}
