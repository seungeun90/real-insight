package io.insight.real.service;

import io.insight.real.dto.DistrictData;
import io.insight.real.infra.repository.jpa.DistrictRepository;
import io.insight.real.infra.repository.mapper.DistrictMapper;
import io.insight.real.service.in.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class DistrictServiceImpl implements DistrictService {
    private final DistrictRepository districtRepository;
    private final DistrictMapper districtMapper;
    @Override
    public List<DistrictData> getDistricts() {
        return districtMapper.toDtoList(districtRepository.findAll());
    }
}
