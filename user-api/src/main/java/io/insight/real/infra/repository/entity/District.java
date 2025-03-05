package io.insight.real.infra.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Document(collection = "district")
public class District {
    @Id
    private String id; // 기본 키
    private String provinceName;
    private List<City> cities;

}
