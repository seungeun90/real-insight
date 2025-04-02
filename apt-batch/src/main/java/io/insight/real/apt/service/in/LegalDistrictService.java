package io.insight.real.apt.service.in;

import io.insight.real.apt.repository.jpa.entity.RegionJpa;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LegalDistrictService {
    List<RegionJpa> getRegions();
    void readLegalDistricts(MultipartFile file);
    long getDistrictsCount();


}
