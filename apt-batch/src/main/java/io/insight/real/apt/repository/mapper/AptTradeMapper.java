package io.insight.real.apt.repository.mapper;

import io.insight.real.apt.dto.response.XmlApartmentItem;
import io.insight.real.apt.repository.r2dbc.entity.AptTrade;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AptTradeMapper {

    List<AptTrade> toEntities(List<XmlApartmentItem> item);
    AptTrade toEntity(XmlApartmentItem item);
    List<XmlApartmentItem> toItems(List<AptTrade> entities);
}
