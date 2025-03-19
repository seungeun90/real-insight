package io.insight.real.city.service;

import io.insight.real.city.repository.entity.AdministrativeDistrict;
import io.insight.real.city.repository.jpa.AdministrativeDistrictRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExcelReaderService {
    private final AdministrativeDistrictRepository repository;


    public long getDistrictDataCount(){
        return repository.count();
    }

    @Transactional
    public void readAndInsertExcel(MultipartFile file) {
        List<AdministrativeDistrict> districts = new ArrayList<>();

        // 파일 확장자 체크 (NPE 방지)
        String extension = FilenameUtils.getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        boolean isFirstRow = true;
        try (Workbook workbook = extension.equalsIgnoreCase("xls")
                ? new HSSFWorkbook(file.getInputStream())  // Excel 97-2003 (xls)
                : new XSSFWorkbook(file.getInputStream())) { // Excel 2007+ (xlsx)

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                log.info("city code= {} name={}", getCellValue(row.getCell(2)),getCellValue(row.getCell(3)));
                AdministrativeDistrict district = AdministrativeDistrict.builder()
                        .provinceCode(getCellValue(row.getCell(0)))  // 시도코드
                        .provinceName(getCellValue(row.getCell(1)))  // 시도명칭
                        .cityDistrictCode(getCellValue(row.getCell(2)))  // 시군구코드
                        .cityDistrictName(getCellValue(row.getCell(3)))  // 시군구명칭
                        .townCode(getCellValue(row.getCell(4)))  // 읍면동코드
                        .townName(getCellValue(row.getCell(5)))  // 읍면동명칭
                        .build();

                districts.add(district);

                // 100개씩 Batch Insert 실행
                if (districts.size() % 100 == 0) {
                    repository.saveAll(districts);
                    districts.clear();
                }
            }

            // 🔥 마지막 남은 데이터 한 번만 처리 (불필요한 반복 제거)
            if (!districts.isEmpty()) {
                repository.saveAll(districts);
            }

        } catch (IOException e) {
            throw new RuntimeException("Excel 파일 처리 중 오류 발생", e);
        }
    }
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue()); // 숫자일 경우 정수 변환
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
