package io.insight.real.apt.repository.mapper;

import io.insight.real.apt.dto.response.ApartmentItem;
import io.insight.real.apt.repository.entity.AptTrade;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AptTradeMapper {

    List<AptTrade> toEntities(List<ApartmentItem> item);
    AptTrade toEntity(ApartmentItem item);
    List<ApartmentItem> toItems(List<AptTrade> entities);
}
