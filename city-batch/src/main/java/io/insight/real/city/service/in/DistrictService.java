package io.insight.real.city.service.in;

import io.insight.real.city.repository.entity.AdministrativeDistrict;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DistrictService {
    long getDistrictDataCount();
    void readExcelAndSave(MultipartFile file);
    List<String> getDistinctProvinceCodes();
    List<AdministrativeDistrict> getByProvinceCode(String provinceCode);
}
