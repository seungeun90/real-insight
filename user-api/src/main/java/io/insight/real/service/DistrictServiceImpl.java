package io.insight.real.service;

import io.insight.real.infra.repository.entity.District;
import io.insight.real.infra.repository.jpa.DistrictRepository;
import io.insight.real.service.in.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class DistrictServiceImpl implements DistrictService {
    private final DistrictRepository districtRepository;
    @Override
    public List<District> getDistricts() {
        return districtRepository.findAll();
    }
}
