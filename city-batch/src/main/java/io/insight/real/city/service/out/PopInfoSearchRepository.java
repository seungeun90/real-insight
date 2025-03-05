package io.insight.real.city.service.out;

import io.insight.real.city.repository.dao.PopulationDao;

import java.util.List;

public interface PopInfoSearchRepository {
    List<PopulationDao> groupByAdmCd(String code);
}
