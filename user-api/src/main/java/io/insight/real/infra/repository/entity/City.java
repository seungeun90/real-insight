package io.insight.real.infra.repository.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class City {
    private String cityCode;
    private String cityName;
    private List<String> towns;
}
