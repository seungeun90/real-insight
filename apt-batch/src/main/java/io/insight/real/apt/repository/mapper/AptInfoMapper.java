package io.insight.real.apt.repository.mapper;

import io.insight.real.apt.dto.response.AptIdInfo;
import io.insight.real.apt.repository.entity.AptInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AptInfoMapper {

    List<AptInfo> toEntities(List<AptIdInfo> items);
}
